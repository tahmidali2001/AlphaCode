public class EvalResult 
{
  private EvalType type;
  private int i;
  private double d;
  private boolean b;
  private String str;
  private RecordDeclaration recordDecl;
  private EvalResult [] array;


  public EvalResult() {
    type = EvalType.VOID;
  }
  
  // set the value of the object and infer its type
  public void setValue(int value) {
    type = EvalType.INTEGER;
    i=value;
  }

  public void setValue(double value) {
    type = EvalType.REAL;
    d = value;
  }


  public void setValue(boolean value) {
    type = EvalType.BOOLEAN;
    b = value;
  }

  public void setValue(String value) {
    type = EvalType.STRING;
    str = value;
  }


  public void setValue(RecordDeclaration recordDecl) {
    type = EvalType.RECORD_DECL;
    this.recordDecl = recordDecl;
  }

  public void setValue(EvalResult [] value) {
    type = EvalType.ARRAY;
    array = value;
  }


  
  public int asInteger() {
    if(type == EvalType.INTEGER) {
      return i;
    } else {
      return (int) d;
    }
  }


  public double asReal() {
    if(type == EvalType.REAL) {
      return d;
    } else {
      return (double) i;
    }
  }


  public boolean asBoolean() {
    return b;
  }

  public String asString() {
    return str;
  }

  public RecordDeclaration asRecordDecl() {
    return recordDecl;
  }

  public EvalResult [] asArray() {
    if(type != EvalType.ARRAY) return null;
    return array;
  }


  public EvalType getType() {
    return type;
  }




  public String toString() {
    if(type == EvalType.STRING){
      return str;
    } else if(type == EvalType.INTEGER) {
      return ""+i;
    } else {
      return ""+d;
    }
  }
  
}
