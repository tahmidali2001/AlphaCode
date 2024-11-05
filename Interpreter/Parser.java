  public class Parser
{
  private Lexer lexer;    

  public Parser(Lexer lexer) {
    this.lexer = lexer;
  }

  public ParseTree parse() {
    this.lexer.next();  //linitialize the lexer
    return parseProgram();
  }

  /**
   Attempt to match tok.
   If it matches, return the lexeme. If it does not match, return null.
   */
  private Lexeme match(Token tok) {
    Lexeme l = lexer.cur();
    if(l.tok == tok) {
      // match, advance lexer, return the match
      lexer.next();
      return l;
    } 

    // no match
    return null;
  }


  /**
   * Attempt to match a token. Returns lexeme on match, halts the program on failure.
   */
  private Lexeme mustBe(Token tok) {
    Lexeme l = match(tok);

    if(l == null) {
      System.out.println("Parse Error: " + lexer.cur().toString());
      System.exit(-1);
    }

    return l;
  }

  /**
   * Return true if one of the tokens in the list are
   * currently in the lexer
   */
  private boolean has(Token... tokens) {
    Lexeme l = lexer.cur();
    for(Token t : tokens) {
      if(l.tok == t) return true;
    }
    return false;
  }

  /**
  < Program >    ::= < Program > < Statement >
                   | < Statement >
   */
  private ParseTree parseProgram() {
    Program result = new Program();
    
    while(lexer.cur().tok != Token.EOF && lexer.cur().tok != Token.QUEBEC) {
      ParseTree statement = parseStatement();
      if(statement != null) {
        result.addChild(statement);
      }
    }

    return result;
  }


  /**
  < Statement >  ::= ID < Statement' > NEWLINE
                     | < IO-Operation > NEWLINE
                     | < Array-Dim > NEWLINE
                     | < Branch > NEWLINE
                     | < LOOP > NEWLINE
                     | < Fun-Def > NEWLINE
                     | < Expression > NEWLINE
                     | NEWLINE
  */
  private ParseTree parseStatement() {
    ParseTree result = null;
    Lexeme tok;

    if(has(Token.OSCAR)) { 
      // Handle an ID statement / expression
      result = parseRef();
      result = parseStatement2(result);
    } else if(has(Token.NOVEMBER, Token.MIKE)) {
      result = parseIOOperation();
    } else if(has(Token.ILLUMINATI)) {
      result = parseArrayDimension(); 
    } else if(has(Token.PAPA)) {
      result = parseBranch();
    } else if(has(Token.ROMEO)) {
      result = parseLoop();
    } else if(!has(Token.HOTEL)) {
      result = parseExpression();
    }
    
    if(match(Token.EOF) == null) {
      mustBe(Token.HOTEL);
    }
    return result;
  }

  /**
  < Statement' >  ::= EQUAL < Expression >
                      | < Factor' > < Term' > < Expression' >
  */
  private ParseTree parseStatement2(ParseTree left) {
    // match an assignment
    if(match(Token.LIMA) != null) {
      Assignment result = new Assignment();
      result.setLeft(left);
      result.setRight(parseExpression());
      return result;
    }

    // an expression beginning with an ID
    ParseTree result = parseFactor2(left);
    result = parseTerm2(result);
    result = parseExpression2(result);
    return result;
  }

  /**
  < Array-Dim >  ::= DIMENSION ID < Expression >
  */
  private ParseTree parseArrayDimension() {
    mustBe(Token.ILLUMINATI);
    ParseTree left = new Variable(mustBe(Token.OSCAR));
    ParseTree right = parseExpression();
    ArrayDimension result = new ArrayDimension();
    result.setLeft(left);
    result.setRight(right);

    return result;
  }
  

  /**
   < Branch >     ::= IF < Condition > NEWLINE < Program > END IF
   */
  private ParseTree parseBranch() {
    mustBe(Token.PAPA);
    ParseTree condition = parseCondition();
    mustBe(Token.HOTEL);
    ParseTree program = parseProgram();
    mustBe(Token.QUEBEC);
    mustBe(Token.PAPA);
    
    Branch result = new Branch();
    result.setLeft(condition);
    result.setRight(program);
    return result;
  }

  /**
   < Loop >        ::= WHILE < Condition > NEWLINE < Program > END WHILE
  */
  private ParseTree parseLoop() {
    mustBe(Token.ROMEO);
    ParseTree condition = parseCondition();
    mustBe(Token.HOTEL);
    ParseTree program = parseProgram();
    mustBe(Token.QUEBEC);
    mustBe(Token.ROMEO);

    Loop result = new Loop();
    result.setLeft(condition);
    result.setRight(program);

    return result;
  }


  
  

  /**
   < Condition >  ::= < Expression > < Condition' >
   */
  private ParseTree parseCondition() {
    ParseTree left = parseExpression();
    return parseCondition2(left);
  }


  /**
   < Condition' > ::= EQUAL < Expression > 
   < Condition' > ::= GT < Expression >
                      | LT < Expression >
                      | LTE < Expression >
                      | GTE < Expression >
                      | EQUAL < Expression >
                      | NE < Expression >
   */
  private ParseTree parseCondition2(ParseTree left) {
    BinaryOp result;

    if(match(Token.SIERRA) != null) {
      result = new Greater();
    } else if(match(Token.UNIFORM)!=null) {
      result = new Less(); 
    } else if(match(Token.VICTOR)!=null) {
      result = new LessOrEqual();
    } else if(match(Token.LIMA) != null) {
      result = new Equal();
    } else if(match(Token.TANGO) != null) {
      result = new GreaterOrEqual();
    }else if(mustBe(Token.WHISKEY) != null) {
      result = new NotEqual();
    } else {
      return null;
    }
    
    result.setLeft(left);
    result.setRight(parseExpression());
    
    return result;
  }



  
  
  /** 
  < IO-Operation > ::= DISPLAY < Option >
                       | INPUT < Ref >
  */
  private ParseTree parseIOOperation() {
    if(match(Token.MIKE) != null) {
      Display result = new Display();
      result.setChild(parseOption());
      return result;
    }

    mustBe(Token.NOVEMBER);
    Input result = new Input();
    result.setChild(parseRef());
    return result;
  }
  
  private ParseTree parseOption() {
	  if(match(Token.QUOTATION) != null) {
		  ParseTree result = parseString();
	      mustBe(Token.QUOTATION);
	      return result;
	  }
	  return parseExpression();
	  
  }
  
  private ParseTree parseString() {
	  Lexeme tok = mustBe(Token.ANYTHING);
	     
	  return new Literal(tok);

  }

  /**
  < Term >       ::= < Factor > < Term' >
  */
  private ParseTree parseTerm() {
    ParseTree left = parseFactor();
    return parseTerm2(left);
  }
  
  /**
  < Term' >      ::= TIMES < Factor > < Term' >
                   | DIVIDE < Factor > < Term' >
                   | MOD < Factor > < Term' >
                   | ""
  */
 private ParseTree parseTerm2(ParseTree left) {
    if(match(Token.CHARLIE) != null) {
        Multiply result = new Multiply();
        result.setLeft(left);
        result.setRight(parseFactor());
        return parseTerm2(result);
    } else if(match(Token.DELTA) != null) {
        Divide result = new Divide();
        result.setLeft(left);
        result.setRight(parseTerm());
        return parseTerm2(result);
      
    } else if(match(Token.KILO) != null) {
        Mod result  = new Mod();
        result.setLeft(left);
        result.setRight(parseFactor());
        return parseTerm2(result);
    }
    
    return left; 
  }

  /** 
  < Number >     ::= INTLIT | REALLIT | < Ref >
  */
  private ParseTree parseNumber() {
    Lexeme tok = match(Token.INDIA);
    if (tok != null) { 
      return new Literal(tok);
    }

    if(has(Token.OSCAR)) {
        return parseRef();
    }

    tok = mustBe(Token.JULIETT);
    return new Literal(tok);
  }

    /* 
    < Ref >        ::= ID < Ref' >
    */
    ParseTree parseRef() {
        Lexeme tok = mustBe(Token.OSCAR);

        return parseRef2(new Variable(tok));
    }


    /* 
    < Ref' >       ::= LBRACKET < Expression > RBRACKET < Ref' >
                       | DOT ID < Ref' >
                       | ""
    */
    ParseTree parseRef2(ParseTree left) {
        if(match(Token.XRAY) != null) {
            // array access
            ArrayAccess result = new ArrayAccess();
            result.setLeft(left);
            result.setRight(parseExpression());
            mustBe(Token.YANKEE);
            return parseRef2(result);
        } else if(match(Token.ZULU) != null) {
            // record access
            RecordAccess result = new RecordAccess();
            result.setLeft(left);
            result.setRight(new Variable(mustBe(Token.OSCAR)));
            return parseRef2(result);
        }

        // null string
        return left;
    }

  
  /**
    < Exponent >   ::= < Number >
                   | MINUS < Exponent >
                   | LPAREN < Expression > RPAREN
  */

  private ParseTree parseExponent(){
    if(match(Token.BRAVO) != null){
      Negate result = new Negate();
      result.setChild(parseExponent());
      return result;
    } else if(match(Token.FOXTROT) != null){
      ParseTree result = parseExpression();
      mustBe(Token.GOLF);
      return result;
    } else {
      return parseNumber();
    }
  }

// < Expression > ::= < Term > < Expression' >
  
 private ParseTree parseExpression() {
    ParseTree left = parseTerm();
    return parseExpression2(left);
 }
  
/**
< Expression' > ::= PLUS < Term > < Expression' >
                   | MINUS < Term > < Expression' >
                   | ""
 */
public ParseTree parseExpression2(ParseTree left){
  if (match(Token.ALPHA) != null){
    Add result = new Add();
    result.setLeft(left);
    result.setRight(parseTerm());
    return parseExpression2(result);
  } else if (match(Token.BRAVO) != null){
    Subtract result = new Subtract();
    result.setLeft(left);
    result.setRight(parseTerm());
    return parseExpression2(result);
  } 

  // ""
  return left;
}

    /*
    < Factor >     ::= < Exponent > < Factor' >

    */
    public ParseTree parseFactor(){
      ParseTree left = parseExponent();
      return parseFactor2(left);
    }


    /**
    < Factor' >    ::= POW < Exponent > < Factor' >
                      | ""
    */
    public ParseTree parseFactor2(ParseTree left){
      if (match(Token.ECHO) != null){
        Power result = new Power();
        result.setLeft(left);
        result.setRight(parseExponent());
        return parseFactor2(result);
      }

      return left;
    }
  
 

  /** 
   * Test the parser
   */
  public static void main(String [] args) {
    Lexer lexer = new Lexer(System.in);
    Parser parser = new Parser(lexer);

    parser.parse().print(0);
  }
}
