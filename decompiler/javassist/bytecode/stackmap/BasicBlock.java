package javassist.bytecode.stackmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ExceptionTable;
import javassist.bytecode.MethodInfo;















public class BasicBlock
{
  protected int position;
  protected int length;
  protected int incoming;
  protected BasicBlock[] exit;
  protected boolean stop;
  protected Catch toCatch;
  
  static class JsrBytecode
    extends BadBytecode
  {
    private static final long serialVersionUID = 1L;
    
    JsrBytecode() {
      super("JSR");
    }
  }





  
  protected BasicBlock(int pos) {
    this.position = pos;
    this.length = 0;
    this.incoming = 0;
  }


  
  public static BasicBlock find(BasicBlock[] blocks, int pos) throws BadBytecode {
    for (BasicBlock b : blocks) {
      if (b.position <= pos && pos < b.position + b.length)
        return b; 
    } 
    throw new BadBytecode("no basic block at " + pos);
  }
  
  public static class Catch { public Catch next;
    public BasicBlock body;
    public int typeIndex;
    
    Catch(BasicBlock b, int i, Catch c) {
      this.body = b;
      this.typeIndex = i;
      this.next = c;
    } }


  
  public String toString() {
    StringBuffer sbuf = new StringBuffer();
    String cname = getClass().getName();
    int i = cname.lastIndexOf('.');
    sbuf.append((i < 0) ? cname : cname.substring(i + 1));
    sbuf.append("[");
    toString2(sbuf);
    sbuf.append("]");
    return sbuf.toString();
  }
  
  protected void toString2(StringBuffer sbuf) {
    sbuf.append("pos=").append(this.position).append(", len=")
      .append(this.length).append(", in=").append(this.incoming)
      .append(", exit{");
    if (this.exit != null)
      for (BasicBlock b : this.exit) {
        sbuf.append(b.position).append(",");
      } 
    sbuf.append("}, {");
    Catch th = this.toCatch;
    while (th != null) {
      sbuf.append("(").append(th.body.position).append(", ")
        .append(th.typeIndex).append("), ");
      th = th.next;
    } 
    
    sbuf.append("}");
  }

  
  static class Mark
    implements Comparable<Mark>
  {
    int position;
    
    BasicBlock block;
    BasicBlock[] jump;
    boolean alwaysJmp;
    int size;
    BasicBlock.Catch catcher;
    
    Mark(int p) {
      this.position = p;
      this.block = null;
      this.jump = null;
      this.alwaysJmp = false;
      this.size = 0;
      this.catcher = null;
    }

    
    public int compareTo(Mark obj) {
      if (null == obj)
        return -1; 
      return this.position - obj.position;
    }
    
    void setJump(BasicBlock[] bb, int s, boolean always) {
      this.jump = bb;
      this.size = s;
      this.alwaysJmp = always;
    }
  }


  
  public static class Maker
  {
    protected BasicBlock makeBlock(int pos) {
      return new BasicBlock(pos);
    }
    
    protected BasicBlock[] makeArray(int size) {
      return new BasicBlock[size];
    }
    
    private BasicBlock[] makeArray(BasicBlock b) {
      BasicBlock[] array = makeArray(1);
      array[0] = b;
      return array;
    }
    
    private BasicBlock[] makeArray(BasicBlock b1, BasicBlock b2) {
      BasicBlock[] array = makeArray(2);
      array[0] = b1;
      array[1] = b2;
      return array;
    }
    
    public BasicBlock[] make(MethodInfo minfo) throws BadBytecode {
      CodeAttribute ca = minfo.getCodeAttribute();
      if (ca == null) {
        return null;
      }
      CodeIterator ci = ca.iterator();
      return make(ci, 0, ci.getCodeLength(), ca.getExceptionTable());
    }



    
    public BasicBlock[] make(CodeIterator ci, int begin, int end, ExceptionTable et) throws BadBytecode {
      Map<Integer, BasicBlock.Mark> marks = makeMarks(ci, begin, end, et);
      BasicBlock[] bb = makeBlocks(marks);
      addCatchers(bb, et);
      return bb;
    }


    
    private BasicBlock.Mark makeMark(Map<Integer, BasicBlock.Mark> table, int pos) {
      return makeMark0(table, pos, true, true);
    }




    
    private BasicBlock.Mark makeMark(Map<Integer, BasicBlock.Mark> table, int pos, BasicBlock[] jump, int size, boolean always) {
      BasicBlock.Mark m = makeMark0(table, pos, false, false);
      m.setJump(jump, size, always);
      return m;
    }

    
    private BasicBlock.Mark makeMark0(Map<Integer, BasicBlock.Mark> table, int pos, boolean isBlockBegin, boolean isTarget) {
      Integer p = Integer.valueOf(pos);
      BasicBlock.Mark m = table.get(p);
      if (m == null) {
        m = new BasicBlock.Mark(pos);
        table.put(p, m);
      } 
      
      if (isBlockBegin) {
        if (m.block == null) {
          m.block = makeBlock(pos);
        }
        if (isTarget) {
          m.block.incoming++;
        }
      } 
      return m;
    }



    
    private Map<Integer, BasicBlock.Mark> makeMarks(CodeIterator ci, int begin, int end, ExceptionTable et) throws BadBytecode {
      ci.begin();
      ci.move(begin);
      Map<Integer, BasicBlock.Mark> marks = new HashMap<>();
      while (ci.hasNext()) {
        int index = ci.next();
        if (index >= end) {
          break;
        }
        int op = ci.byteAt(index);
        if ((153 <= op && op <= 166) || op == 198 || op == 199) {
          
          BasicBlock.Mark to = makeMark(marks, index + ci.s16bitAt(index + 1));
          BasicBlock.Mark next = makeMark(marks, index + 3);
          makeMark(marks, index, makeArray(to.block, next.block), 3, false); continue;
        } 
        if (167 <= op && op <= 171) {
          int pos; int low; int ncases; int high; BasicBlock[] to; int i; int p; BasicBlock[] arrayOfBasicBlock1; int n; int j; int k; int m; int i1; switch (op) {
            case 167:
              makeGoto(marks, index, index + ci.s16bitAt(index + 1), 3);
              continue;
            case 168:
              makeJsr(marks, index, index + ci.s16bitAt(index + 1), 3);
              continue;
            case 169:
              makeMark(marks, index, null, 2, true);
              continue;
            case 170:
              pos = (index & 0xFFFFFFFC) + 4;
              low = ci.s32bitAt(pos + 4);
              high = ci.s32bitAt(pos + 8);
              i = high - low + 1;
              arrayOfBasicBlock1 = makeArray(i + 1);
              arrayOfBasicBlock1[0] = (makeMark(marks, index + ci.s32bitAt(pos))).block;
              j = pos + 12;
              m = j + i * 4;
              i1 = 1;
              while (j < m) {
                arrayOfBasicBlock1[i1++] = (makeMark(marks, index + ci.s32bitAt(j))).block;
                j += 4;
              } 
              makeMark(marks, index, arrayOfBasicBlock1, m - index, true);
              continue;
            case 171:
              pos = (index & 0xFFFFFFFC) + 4;
              ncases = ci.s32bitAt(pos + 4);
              to = makeArray(ncases + 1);
              to[0] = (makeMark(marks, index + ci.s32bitAt(pos))).block;
              p = pos + 8 + 4;
              n = p + ncases * 8 - 4;
              k = 1;
              while (p < n) {
                to[k++] = (makeMark(marks, index + ci.s32bitAt(p))).block;
                p += 8;
              } 
              makeMark(marks, index, to, n - index, true); continue;
          }  continue;
        } 
        if ((172 <= op && op <= 177) || op == 191) {
          makeMark(marks, index, null, 1, true); continue;
        }  if (op == 200) {
          makeGoto(marks, index, index + ci.s32bitAt(index + 1), 5); continue;
        }  if (op == 201) {
          makeJsr(marks, index, index + ci.s32bitAt(index + 1), 5); continue;
        }  if (op == 196 && ci.byteAt(index + 1) == 169) {
          makeMark(marks, index, null, 4, true);
        }
      } 
      if (et != null) {
        int i = et.size();
        while (--i >= 0) {
          makeMark0(marks, et.startPc(i), true, false);
          makeMark(marks, et.handlerPc(i));
        } 
      } 
      
      return marks;
    }
    
    private void makeGoto(Map<Integer, BasicBlock.Mark> marks, int pos, int target, int size) {
      BasicBlock.Mark to = makeMark(marks, target);
      BasicBlock[] jumps = makeArray(to.block);
      makeMark(marks, pos, jumps, size, true);
    }










    
    protected void makeJsr(Map<Integer, BasicBlock.Mark> marks, int pos, int target, int size) throws BadBytecode {
      throw new BasicBlock.JsrBytecode();
    }
    private BasicBlock[] makeBlocks(Map<Integer, BasicBlock.Mark> markTable) {
      BasicBlock prev;
      BasicBlock.Mark[] marks = (BasicBlock.Mark[])markTable.values().toArray((Object[])new BasicBlock.Mark[markTable.size()]);
      Arrays.sort((Object[])marks);
      List<BasicBlock> blocks = new ArrayList<>();
      int i = 0;
      
      if (marks.length > 0 && (marks[0]).position == 0 && (marks[0]).block != null) {
        prev = getBBlock(marks[i++]);
      } else {
        prev = makeBlock(0);
      } 
      blocks.add(prev);
      while (i < marks.length) {
        BasicBlock.Mark m = marks[i++];
        BasicBlock bb = getBBlock(m);
        if (bb == null) {
          
          if (prev.length > 0) {
            
            prev = makeBlock(prev.position + prev.length);
            blocks.add(prev);
          } 
          
          prev.length = m.position + m.size - prev.position;
          prev.exit = m.jump;
          prev.stop = m.alwaysJmp;
          
          continue;
        } 
        if (prev.length == 0) {
          prev.length = m.position - prev.position;
          bb.incoming++;
          prev.exit = makeArray(bb);

        
        }
        else if (prev.position + prev.length < m.position) {
          
          prev = makeBlock(prev.position + prev.length);
          blocks.add(prev);
          prev.length = m.position - prev.position;

          
          prev.stop = true;
          prev.exit = makeArray(bb);
        } 

        
        blocks.add(bb);
        prev = bb;
      } 

      
      return blocks.<BasicBlock>toArray(makeArray(blocks.size()));
    }
    
    private static BasicBlock getBBlock(BasicBlock.Mark m) {
      BasicBlock b = m.block;
      if (b != null && m.size > 0) {
        b.exit = m.jump;
        b.length = m.size;
        b.stop = m.alwaysJmp;
      } 
      
      return b;
    }


    
    private void addCatchers(BasicBlock[] blocks, ExceptionTable et) throws BadBytecode {
      if (et == null) {
        return;
      }
      int i = et.size();
      while (--i >= 0) {
        BasicBlock handler = BasicBlock.find(blocks, et.handlerPc(i));
        int start = et.startPc(i);
        int end = et.endPc(i);
        int type = et.catchType(i);
        handler.incoming--;
        for (int k = 0; k < blocks.length; k++) {
          BasicBlock bb = blocks[k];
          int iPos = bb.position;
          if (start <= iPos && iPos < end) {
            bb.toCatch = new BasicBlock.Catch(handler, type, bb.toCatch);
            handler.incoming++;
          } 
        } 
      } 
    }
  }
}
