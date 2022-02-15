package com.kichik.pecoff4j.asm;

import com.kichik.pecoff4j.util.Reflection;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;











public class AssemblyParser
{
  public static AbstractInstruction[] parseAll(int offset, InputStream is) throws IOException {
    List<AbstractInstruction> instructions = new ArrayList<>();
    AbstractInstruction ins = null;
    while ((ins = parse(is)) != null) {
      ins.setOffset(offset);
      offset += ins.size();
      instructions.add(ins);
    } 
    return instructions
      .<AbstractInstruction>toArray(new AbstractInstruction[instructions.size()]);
  }
  
  public static AbstractInstruction parse(InputStream is) throws IOException {
    int imm32, disp32, opcode = is.read() & 0xFF;
    int highop = opcode & 0xF0;

    
    ModRM modrm = null;
    SIB sib = null;
    switch (highop) {
      case 0:
        switch (opcode) {
          case 3:
            modrm = new ModRM(is.read());
            imm32 = readDoubleWord(is);
            return new ADD(opcode, modrm, imm32);
          case 15:
            return new JumpIfInstruction(is.read(), readDoubleWord(is));
        } 
        break;
      case 48:
        switch (opcode) {
          case 59:
            modrm = new ModRM(is.read());
            imm32 = is.read();
            return new CMP(modrm, (byte)imm32);
        } 
        break;
      case 80:
        if (opcode < 88) {
          return new PUSH(opcode & 0xF);
        }
        return new POP(opcode >> 4 & 0xF);
      
      case 96:
        switch (opcode) {
          case 104:
            return new PUSH(opcode, readDoubleWord(is));
          case 106:
            return new PUSH((byte)is.read());
        } 
        break;
      case 112:
        switch (opcode) {
          case 125:
            return new JGE((byte)is.read());
        } 
        break;
      case 128:
        modrm = new ModRM(is.read());
        switch (opcode) {
          case 139:
            if (modrm.mod < 3 && modrm.reg1 == 4)
              sib = new SIB(is.read()); 
            switch (modrm.mod) {
              case 0:
              case 1:
                imm32 = is.read();
                if (sib != null) {
                  return new MOV(modrm, sib, (byte)imm32);
                }
                return new MOV(modrm, (byte)imm32);
              case 2:
                imm32 = readDoubleWord(is);
                if (sib != null) {
                  return new MOV(opcode, modrm, sib, imm32);
                }
                return new MOV(opcode, modrm, imm32);
            } 
            return new MOV(modrm);
          case 129:
            imm32 = readDoubleWord(is);
            return new SUB(modrm, imm32);
          case 131:
            imm32 = is.read();
            return new ADD(modrm, (byte)imm32);
          case 137:
            switch (modrm.mod) {
              case 0:
              case 1:
                imm32 = is.read();
                return new MOV(opcode, modrm, (byte)imm32);
              case 2:
                imm32 = readDoubleWord(is);
                return new MOV(modrm, imm32);
            } 
          case 133:
            return new TEST(modrm);
          case 141:
            if (modrm.mod < 3 && modrm.reg1 == 4) {
              sib = new SIB(is.read());
              imm32 = readDoubleWord(is);
              return new LEA(modrm, sib, imm32);
            } 
            imm32 = readDoubleWord(is);
            return new LEA(modrm, imm32);
        } 
        print(modrm);
        break;
      case 160:
        switch (opcode) {
          case 161:
          case 163:
            return new MOV(opcode, readDoubleWord(is));
        } 
        break;
      case 192:
        switch (opcode) {
          case 193:
            modrm = new ModRM(is.read());
            imm32 = is.read();
            return new SHL(modrm, (byte)imm32);
          case 195:
            return new RET();
          case 198:
            modrm = new ModRM(is.read());
            imm32 = is.read();
            return new MOV(opcode, modrm, (byte)imm32);
          case 199:
            modrm = new ModRM(is.read());
            switch (modrm.mod) {
              case 1:
                disp32 = is.read();
                imm32 = readDoubleWord(is);
                return new MOV(modrm, (byte)disp32, imm32);
            } 
            disp32 = readDoubleWord(is);
            imm32 = readDoubleWord(is);
            return new MOV(modrm, disp32, imm32);
        } 
        break;
      case 224:
        switch (opcode) {
          case 232:
            return new CALL(opcode, readDoubleWord(is));
          case 233:
            return new JMP(readDoubleWord(is));
          case 235:
            return new JMP((byte)is.read());
        } 
        break;
      case 240:
        switch (opcode) {
          case 255:
            modrm = new ModRM(is.read());
            imm32 = readDoubleWord(is);
            return new CALL(modrm, imm32);
        } 
        break;
    } 
    println(Integer.valueOf(opcode));
    return null;
  }
  
  public static int readDoubleWord(InputStream is) throws IOException {
    return is.read() | is.read() << 8 | is.read() << 16 | is.read() << 24;
  }
  
  public static void print(Object o) {
    System.out.print(Reflection.toString(o));
  }
  
  public static void println(Object o) {
    System.out.println(Reflection.toString(o));
  }
}
