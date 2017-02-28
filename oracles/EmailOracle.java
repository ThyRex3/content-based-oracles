/*
<http://tools.ietf.org/html/rfc2822>

3.2.4. Atom
   Several productions in structured header field bodies are simply
   strings of certain basic characters.  Such productions are called
   atoms.

   Some of the structured header field bodies also allow the period
   character (".", ASCII value 46) within runs of atext.  An additional
   "dot-atom" token is defined for those purposes.

   Both atom and dot-atom are interpreted as a single unit, comprised of
   the string of characters that make it up.  Semantically, the optional
   comments and FWS surrounding the rest of the characters are not part
   of the atom; the atom is only the run of atext characters in an atom,
   or the atext and "." characters in a dot-atom.

atext           =       ALPHA / DIGIT / ; Any character except controls,
                        "!" / "#" /     ;  SP, and specials.
                        "$" / "%" /     ;  Used for atoms
                        "&" / "'" /
                        "*" / "+" /
                        "-" / "/" /
                        "=" / "?" /
                        "^" / "_" /
                        "`" / "{" /
                        "|" / "}" /
                        "~"

atom            =       [CFWS] 1*atext [CFWS]

dot-atom        =       [CFWS] dot-atom-text [CFWS]

dot-atom-text   =       1*atext *("." 1*atext)

3.2.6. Miscellaneous tokens
   Three additional tokens are defined, word and phrase for combinations
   of atoms and/or quoted-strings, and unstructured for use in
   unstructured header fields and in some places within structured
   header fields.

word            =       atom / quoted-string

phrase          =       1*word / obs-phrase

utext           =       NO-WS-CTL /     ; Non white space controls
                        %d33-126 /      ; The rest of US-ASCII
                        obs-utext

unstructured    =       *([FWS] utext) [FWS]

4.4. Obsolete Addressing
   There are three primary differences in addressing.  First, mailbox
   addresses were allowed to have a route portion before the addr-spec
   when enclosed in "<" and ">".  The route is simply a comma-separated
   list of domain names, each preceded by "@", and the list terminated
   by a colon.  Second, CFWS were allowed between the period-separated
   elements of local-part and domain (i.e., dot-atom was not used).  In
   addition, local-part is allowed to contain quoted-string in addition
   to just atom.  Finally, mailbox-list and address-list were allowed to
   have "null" members.  That is, there could be two or more commas in
   such a list with nothing in between them.

   When interpreting addresses, the route portion SHOULD be ignored.

obs-angle-addr  =       [CFWS] "<" [obs-route] addr-spec ">" [CFWS]

obs-route       =       [CFWS] obs-domain-list ":" [CFWS]

obs-domain-list =       "@" domain *(*(CFWS / "," ) [CFWS] "@" domain)

obs-local-part  =       word *("." word)

obs-domain      =       atom *("." atom)

obs-mbox-list   =       1*([mailbox] [CFWS] "," [CFWS]) [mailbox]

obs-addr-list   =       1*([address] [CFWS] "," [CFWS]) [address]

3.2.1. Primitive Tokens
   The following are primitive tokens referred to elsewhere in this
   standard, but not otherwise defined in [RFC2234].  Some of them will
   not appear anywhere else in the syntax, but they are convenient to
   refer to in other parts of this document.

   Note: The "specials" below are just such an example.  Though the
   specials token does not appear anywhere else in this standard, it is
   useful for implementers who use tools that lexically analyze
   messages.  Each of the characters in specials can be used to indicate
   a tokenization point in lexical analysis.

   No special semantics are attached to these tokens.  They are simply
   single characters.

NO-WS-CTL       =       %d1-8 /         ; US-ASCII control characters
                        %d11 /          ;  that do not include the
                        %d12 /          ;  carriage return, line feed,
                        %d14-31 /       ;  and white space characters
                        %d127

text            =       %d1-9 /         ; Characters excluding CR and LF
                        %d11 /
                        %d12 /
                        %d14-127 /
                        obs-text

specials        =       "(" / ")" /     ; Special characters used in
                        "<" / ">" /     ;  other parts of the syntax
                        "[" / "]" /
                        ":" / ";" /
                        "@" / "\" /
                        "," / "." /
                        DQUOTE

3.2.2. Quoted characters
   Some characters are reserved for special interpretation, such as
   delimiting lexical tokens.  To permit use of these characters as
   uninterpreted data, a quoting mechanism is provided.

   Where any quoted-pair appears, it is to be interpreted as the text
   character alone.  That is to say, the "\" character that appears as
   part of a quoted-pair is semantically "invisible".

   Note: The "\" character may appear in a message where it is not part
   of a quoted-pair.  A "\" character that does not appear in a
   quoted-pair is not semantically invisible.  The only places in this
   standard where quoted-pair currently appears are ccontent, qcontent,
   dcontent, no-fold-quote, and no-fold-literal.

quoted-pair     =       ("\" text) / obs-qp

4.1. Miscellaneous obsolete tokens
   These syntactic elements are used elsewhere in the obsolete syntax or
   in the main syntax.  The obs-char and obs-qp elements each add ASCII
   value 0. Bare CR and bare LF are added to obs-text and obs-utext.
   The period character is added to obs-phrase. The obs-phrase-list
   provides for "empty" elements in a comma-separated list of phrases.

   Note: The "period" (or "full stop") character (".") in obs-phrase is
   not a form that was allowed in earlier versions of this or any other
   standard.  Period (nor any other character from specials) was not
   allowed in phrase because it introduced a parsing difficulty
   distinguishing between phrases and portions of an addr-spec (see
   section 4.4).  It appears here because the period character is
   currently used in many messages in the display-name portion of
   addresses, especially for initials in names, and therefore must be
   interpreted properly.  In the future, period may appear in the
   regular syntax of phrase.

   Bare CR and bare LF appear in messages with two different meanings.
   In many cases, bare CR or bare LF are used improperly instead of CRLF
   to indicate line separators.  In other cases, bare CR and bare LF are
   used simply as ASCII control characters with their traditional ASCII
   meanings.

obs-qp          =       "\" (%d0-127)

obs-text        =       *LF *CR *(obs-char *LF *CR)

obs-char        =       %d0-9 / %d11 /          ; %d0-127 except CR and
                        %d12 / %d14-127         ;  LF

obs-utext       =       obs-text

obs-phrase      =       word *(word / "." / CFWS)

obs-phrase-list =       phrase / 1*([phrase] [CFWS] "," [CFWS]) [phrase]

3.4.1. Addr-spec specification
   An addr-spec is a specific Internet identifier that contains a
   locally interpreted string followed by the at-sign character ("@",
   ASCII value 64) followed by an Internet domain.  The locally
   interpreted string is either a quoted-string or a dot-atom.  If the
   string can be represented as a dot-atom (that is, it contains no
   characters other than atext characters or "." surrounded by atext
   characters), then the dot-atom form SHOULD be used and the
   quoted-string form SHOULD NOT be used. Comments and folding white
   space SHOULD NOT be used around the "@" in the addr-spec.

   The domain portion identifies the point to which the mail is
   delivered. In the dot-atom form, this is interpreted as an Internet
   domain name (either a host name or a mail exchanger name) as
   described in [STD3, STD13, STD14].  In the domain-literal form, the
   domain is interpreted as the literal Internet address of the
   particular host.  In both cases, how addressing is used and how
   messages are transported to a particular host is covered in the mail
   transport document [RFC2821].  These mechanisms are outside of the
   scope of this document.

   The local-part portion is a domain dependent string.  In addresses,
   it is simply interpreted on the particular host as a name of a
   particular mailbox.

addr-spec       =       local-part "@" domain

local-part      =       dot-atom / quoted-string / obs-local-part

domain          =       dot-atom / domain-literal / obs-domain

domain-literal  =       [CFWS] "[" *([FWS] dcontent) [FWS] "]" [CFWS]

dcontent        =       dtext / quoted-pair

dtext           =       NO-WS-CTL /     ; Non white space controls

                        %d33-90 /       ; The rest of the US-ASCII
                        %d94-126        ;  characters not including "[",
                                        ;  "]", or "\"
*/
/**
 *   For this recognizer I ignored the obsolete parts of an email address.  They
 * are commented out and untested.  All the parts of the 'grammar' are broken up
 * into the individual components.
 */


package oracles;

import java.util.regex.Pattern;

import dataStructures.StringLookup;
import supportMethods.Setup;
import supportMethods.BasicAttributes;
import supportObjects.NameValuePair;


public class EmailOracle implements Oracle  {
  private int ml,  //Maximum length.
              gid;  //Grouping ID.
  private double mp,  //Minimum percentage.
                 mbp;  //Maximum blank percentage.
  private StringLookup lookup;
  private String on,  //The oracle's name.
                 dbfn;  //The file name of the supporting database.
  private Pattern p;

  private static String atext = "[\\w!#$%&'*+-/=?^`{}~]";
  private static String atom = ( atext + "+" );
  private static String quotedString = "(\"(.)+\")";
  private static String dotAtom = ( atom + "(\\." + atom + ")*" );
  private static String localPart = ( "(" + dotAtom + "|" +
                                      quotedString + ")" );
  private static String noWsCtl =
    "[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]";
  private static String dtext = ( noWsCtl +
                                  "|[\\x21-\\x5A]|[\\x5E-\\x7E]" );
  private static String text = "[\\x01-\\x7F&&[^\\x0A\\x0D]]";
  private static String quotedPair = ( "\\" + text );
  private static String dcontent = ( "(" + dtext + "|" + quotedPair + ")" );
  private static String domainLiteral = ( "[\\[](" + dcontent + ")*[\\]]" );
  private static String domain = ( "(" + dotAtom + "|" + domainLiteral + ")" );
  private static String addrSpec = ( "^" + localPart + "@" + domain + "$" );


  public EmailOracle( )  {  }  /*  Constructor  */


  public void initialize( OracleGroup og, String[] args )  {
    int i;
    NameValuePair nvp;
    String n;

    ml = 100;
    gid = 16;
    mp = 0.15;
    mbp = 1.00;
    lookup = new StringLookup( );
    p = Pattern.compile( addrSpec );
    on = "email";
    dbfn = "/data/EMAIL-US-TLD.dat";

    if( args != null )  {
      for( i = 0; i < args.length; i++ )  {
        nvp = Setup.separate( args[i] );

        n = nvp.getName( );
        if( n.equalsIgnoreCase( "typename" ) )  {
          on = nvp.getValue( );
        }
        else if( n.equalsIgnoreCase( "maximumsourcefile" ) )  {
          dbfn = nvp.getValue( );
        }
        else if( n.equalsIgnoreCase( "maximumlength" ) )  {
          try  {
            ml = Integer.parseInt( nvp.getValue( ) );
          }catch( NumberFormatException e )  {
            ml = 100;
          }
        }
        else if( n.equalsIgnoreCase( "minimumthreshold" ) )  {
          try  {
            mp = Double.parseDouble( nvp.getValue( ) );
          }catch( NumberFormatException e )  {
            mp = 0.15;
          }
        }
        else if( n.equalsIgnoreCase( "maximumblankpercentage" ) )  {
          try  {
            mbp = Double.parseDouble( nvp.getValue( ) );
          }catch( NumberFormatException e )  {
            mbp = 1.00;
          }
        }
        else if( n.equalsIgnoreCase( "grouping" ) )  {
          try  {
            gid = Integer.parseInt( nvp.getValue( ), 2 );
          }catch( NumberFormatException e )  {
            gid = 16;
          }
        }
      }
    }

    lookup.setDataSource(
      og.fileLineCount( BasicAttributes.extractFileName( true, dbfn ) ), dbfn );

  }  /*  public void initialize( OracleGroup, String[] )  */


  public boolean isValid( char[] email )  {
    return( isValid( new String( email ) ) );
  }  /*  public boolean isValid( char[] )  */


  public boolean isValid( char[] email, int beginIndex, int length )  {
    return( isValid( new String( email, beginIndex, length ) ) );
  }  /*  public boolean isValid( char[], int, int )  */


  public boolean isValid( String email )  {
    boolean ret = p.matcher( email.trim( ) ).matches( );
    int i;

    if( ret )  {
      i = email.lastIndexOf( '.' );  //Isolate the TLD.

      if( ret = ( i >= 0 ) )  {
        ret = ( lookup.search( email.substring( i + 1 ).trim( ).
                                 toLowerCase( ) ) >= 0 );

      }
    }
    return( p.matcher( email.trim( ) ).matches( ) );
  }  /*  public boolean isValid( String )  */


  public int getMaxLength( )  {
    return( ml );
  }  /*  public int getMaxLength( )  */


  public int getGrouping( )  {
    return( gid );
  }  /*  public int getGrouping( )  */


  public double getMinPercentage( )  {
    return( mp );
  }  /*  public double getMinPercentage( )  */


  public double getMaxBlankPercentage( )  {
    return( mbp );
  }  /*  public double getMaxBlankPercentage( )  */


  public double matchHeader( String label )  {
    return( 0.0 );
  }  /*  public double matchHeader( String )  */


  public String getName( )  {
    return( on );
  }  /*  public String getName( )  */

}  /*  public class Email  */
