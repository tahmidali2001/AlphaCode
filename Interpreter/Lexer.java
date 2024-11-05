import java.io.IOException;
import java.io.InputStream;

/**
 * Lexer for the calc language
 */
public class Lexer {
  /// Source of the character stream
  private InputStream file;

  /// Current character being matched
  private char cur;

  /// Current Lexeme
  private Lexeme curLex;

  /// Line and column number of input
  private int line;
  private int col;
  private int startLine;
  private int startCol;
  private boolean eof;
  private boolean strStart = false;

  /// The lexeme that we are accumulating
  private StringBuilder curString;

  /// Construct a lexer for the given input stream
  public Lexer(InputStream file) {
    this.file = file;
    this.line = 1;
    this.col = 0;
    this.eof = false;
    read();
  }

  private void setToken(Token tok) {
    curLex = new Lexeme(tok, curString.toString(), startLine, startCol);
  }

  // skip characters we wish to ignore
  private void skip() {
    // skip all the blank characters
    while (cur != '\n' && (Character.isSpaceChar(cur)|| cur == '#'|| cur =='\r')) {
      if(cur == '#') {
        skipToEOL();
      } else { 
        read();
      }
    }
  }

  // skip to the end of the line
  private void skipToEOL() {
    while (!eof && cur != '\n') {
      read();
    }
  }

  public Lexeme next() {
    // start the matching
    skip();
    curString = new StringBuilder();
    startLine = line;
    startCol = col;

    

    // handle eof
    if (eof) {
      setToken(Token.EOF);
      return curLex;
    }

    if (matchAnything())
      return curLex;
    else if (match_single())
      return curLex;
    else if (match_number())
      return curLex;
    else if (matchWord())
      return curLex;
    

      consume();

    // invalid
    setToken(Token.INVALID);
    return curLex;
  }

  public Lexeme cur() {
    return curLex;
  }

  /// Match our single character tokens
  /// Return true on success, false on failure.
  public boolean match_single() {
    Token tok;

    switch (cur) {
      case '\n':
        tok = Token.HOTEL;
        break;
      case '"':
        tok = Token.QUOTATION;
        strStart = !strStart;
        break;
      default:
        tok = Token.INVALID;
    }

    // did not match
    if (tok == Token.INVALID) {
      return false;
    }

    // match
    consume();
    setToken(tok);
    return true;
  }

  /// Match an integer
  private void consumeInteger() {
    while (Character.isDigit(cur)) {
      consume();
    }
  }

  /// Match a Number
  private boolean match_number() {
    if (!Character.isDigit(cur))
      return false;

    // this is an integer
    Token tok = Token.INDIA;
    consumeInteger();

    // check for a dot
    if (cur != '.') {
      setToken(tok);
      return true;
    }

    // it is a dot!
    consume();
    if (Character.isDigit(cur)) {
      tok = Token.JULIETT;
      consumeInteger();
    } else {
      tok = Token.INVALID;
    }
    setToken(tok);
    return true;
  }

  private boolean matchWord() {
    if (!Character.isAlphabetic(cur) && cur != '_')
      return false;

    // consume letters numbers and _
    while (Character.isDigit(cur) || Character.isAlphabetic(cur) || cur == '_') {
      consume();
    }

    // match against keywords
    String str = curString.toString();
    if (str.equals("ALPHA")) {
      setToken(Token.ALPHA);
    } else if (str.equals("BRAVO")) {
      setToken(Token.BRAVO);
    } else if(str.equals("CHARLIE")) {
      setToken(Token.CHARLIE);
    } else if(str.equals("DELTA")) {
      setToken(Token.DELTA);
    } else if(str.equals("ECHO")) {
      setToken(Token.ECHO);
    } else if(str.equals("FOXTROT")) {
      setToken(Token.FOXTROT);
    } else if(str.equals("GOLF")) {
      setToken(Token.GOLF);
    } else if(str.equals("HOTEL")) {
      setToken(Token.HOTEL);
    } else if(str.equals("KILO")) {
      setToken(Token.KILO);
    } else if(str.equals("LIMA")) {
      setToken(Token.LIMA);
    } else if(str.equals("MIKE")) {
      setToken(Token.MIKE);
    } else if(str.equals("NOVEMBER")) {
      setToken(Token.NOVEMBER);
    } else if(str.equals("PAPA")) {
      setToken(Token.PAPA);
    } else if(str.equals("QUEBEC")) {
      setToken(Token.QUEBEC);
    } else if(str.equals("ROMEO")) {
      setToken(Token.ROMEO);
    } else if(str.equals("SIERRA")) {
      setToken(Token.SIERRA);
    } else if(str.equals("TANGO")) {
      setToken(Token.TANGO);
    } else if(str.equals("UNIFORM")) {
      setToken(Token.UNIFORM);
    } else if(str.equals("VICTOR")) {
      setToken(Token.VICTOR);
    } else if(str.equals("WHISKEY")) {
      setToken(Token.WHISKEY);
    } else if(str.equals("XRAY")) {
      setToken(Token.XRAY);
    } else if(str.equals("YANKEE")) {
      setToken(Token.YANKEE);
    } else if(str.equals("ZULU")) {
      setToken(Token.ZULU);
    } else if(str.equals("ILLUMINATI")) {
      setToken(Token.ILLUMINATI);
    } else if(str.equals("FIVEEYES")) {
      setToken(Token.FIVEEYES);
    } else if(str.equals("NATO")) {
      setToken(Token.NATO);
    } else {
      //if it's not a keyword, it's an ID
      setToken(Token.OSCAR); 
    }

    return true;
  }

  private boolean matchAnything(){

    try{
      if(curLex.tok == Token.QUOTATION && strStart==true){
        while(cur!='"'&&cur!='\r'){
          consume();
        }
        setToken(Token.ANYTHING);
        return true;
      }
      return false;
    }catch(Exception ex){
      return false;
    }
    
  }




  /// Read the next character
  private void read() {
    // handle newline
    if (cur == '\n') {
      line++;
      col = 0;
    }
    try {
      int input = file.read();
      cur = (char) input;
      col++;
      if (input == -1) {
        eof = true;
      }
    } catch (IOException ex) {
      // do nothing for now
      eof = true;
    }
  }

  /// Insert the current character into the curStr
  /// and advanced the lexer
  private void consume() {
    curString.append(cur);
    read();
  }

  public static void main(String[] args) {
    Lexer lex = new Lexer(System.in);
    Lexeme tok;

    do {
      tok = lex.next();
      System.out.println(tok);
    } while (tok.tok != Token.EOF);
  }
}