package nokogiri;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nokogiri.internals.NokogiriHandler;

import org.apache.xerces.parsers.AbstractSAXParser;
import org.cyberneko.html.parsers.SAXParser;
import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyFixnum;
import org.jruby.RubyString;
import org.jruby.anno.JRubyClass;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.xml.sax.SAXException;

@JRubyClass(name="Nokogiri::HTML::SAX::ParserContext", parent="Nokogiri::XML::SAX::ParserContext")
public class HtmlSaxParserContext extends XmlSaxParserContext {
    private SAXParser parser;

    public HtmlSaxParserContext(Ruby ruby, RubyClass rubyClass) {
        super(ruby, rubyClass);
    }

    @Override
    protected AbstractSAXParser createParser() throws SAXException {
        SAXParser parser = new SAXParser();

        try{
            parser.setProperty(
                "http://cyberneko.org/html/properties/names/elems", "lower");
            parser.setProperty(
                "http://cyberneko.org/html/properties/names/attrs", "lower");
            return parser;
        } catch(SAXException ex) {
            throw new SAXException(
                "Problem while creating HTML SAX Parser: " + ex.toString());
        }
    }

    @JRubyMethod(name="memory", meta=true)
    public static IRubyObject parse_memory(ThreadContext context,
                                           IRubyObject klazz,
                                           IRubyObject data,
                                           IRubyObject encoding) {
        HtmlSaxParserContext ctx =
            new HtmlSaxParserContext(context.getRuntime(), (RubyClass) klazz);
        String javaEncoding = findEncoding(context, encoding);
        if (javaEncoding != null) {
            String input = applyEncoding((String) data.toJava(String.class), javaEncoding);
            ByteArrayInputStream istream = new ByteArrayInputStream(input.getBytes());
            ctx.setInputSource(istream);
            ctx.getInputSource().setEncoding(javaEncoding);
        }
        return ctx;
    }
    
    public static enum EncodingType {
        NONE(0, "NONE"),
        UTF_8(1, "UTF-8"),
        UTF16LE(2, "UTF16LE"),
        UTF16BE(3, "UTF16BE"),
        UCS4LE(4, "UCS4LE"),
        UCS4BE(5, "UCS4BE"),
        EBCDIC(6, "EBCDIC"),
        UCS4_2143(7, "ICS4-2143"),
        UCS4_3412(8, "UCS4-3412"),
        UCS2(9, "UCS2"),
        ISO_8859_1(10, "ISO-8859-1"),
        ISO_8859_2(11, "ISO-8859-2"),
        ISO_8859_3(12, "ISO-8859-3"),
        ISO_8859_4(13, "ISO-8859-4"),
        ISO_8859_5(14, "ISO-8859-5"),
        ISO_8859_6(15, "ISO-8859-6"),
        ISO_8859_7(16, "ISO-8859-7"),
        ISO_8859_8(17, "ISO-8859-8"),
        ISO_8859_9(18, "ISO-8859-9"),
        ISO_2022_JP(19, "ISO-2022-JP"),
        SHIFT_JIS(20, "SHIFT-JIS"),
        EUC_JP(21, "EUC-JP"),
        ASCII(22, "ASCII");
        
        private final int value;
        private final String name;
        EncodingType(int value, String name) {
            this.value = value;
            this.name = name;
        }
        
        public int getValue() {
            return value;
        }
        
        public String toString() {
            return name;
        }
    }
    
    private static String findName(int value) {
        EnumSet<EncodingType> set = EnumSet.allOf(EncodingType.class);
        for (EncodingType type : set) {
            if (type.getValue() == value) return type.toString();
        }
        return null;
    }
    
    private static String findEncoding(ThreadContext context, IRubyObject encoding) {
        String rubyEncoding = null;
        if (encoding instanceof RubyString) {
            rubyEncoding = (String)encoding.toJava(String.class);
        } else if (encoding instanceof RubyFixnum) {
            int value = (Integer)encoding.toJava(Integer.class);
            rubyEncoding = findName(value);
        }
        if (rubyEncoding == null) return null;
        try {
            Charset charset = Charset.forName(rubyEncoding);
            return charset.displayName();
        } catch (IllegalCharsetNameException e) {
            throw context.getRuntime().newEncodingCompatibilityError(
                    rubyEncoding + "is not supported in Java.");
        } catch (IllegalArgumentException e) {
            throw context.getRuntime().newInvalidEncoding(
                    "encoding should not be nil");
        }
    }
    
    private static String applyEncoding(String input, String enc) {
        String str = input.toLowerCase();
        int start_pos = 0;
        int end_pos = 0;
        if (input.contains("meta") && input.contains("charset")) {
            Pattern p = Pattern.compile("charset(()|\\s)=(()|\\s)([a-z]|-|_|\\d)+");
            Matcher m = p.matcher(str);
            while (m.find()) {
                start_pos = m.start();
                end_pos = m.end();
            }
        }
        if (start_pos != end_pos) {
            String substr = input.substring(start_pos, end_pos);
            input = input.replace(substr, "charset=" + enc);
        }
        return input;
    }

    @JRubyMethod(name="file", meta=true)
    public static IRubyObject parse_file(ThreadContext context,
                                         IRubyObject klazz,
                                         IRubyObject data,
                                         IRubyObject encoding) {
        HtmlSaxParserContext ctx =
            new HtmlSaxParserContext(context.getRuntime(), (RubyClass) klazz);
        ctx.setInputSourceFile(context, data);
        String javaEncoding = findEncoding(context, encoding);
        if (javaEncoding != null) {
            ctx.getInputSource().setEncoding(javaEncoding);
        }
        return ctx;
    }

    @JRubyMethod(name="io", meta=true)
    public static IRubyObject parse_io(ThreadContext context,
                                       IRubyObject klazz,
                                       IRubyObject data,
                                       IRubyObject encoding) {
        HtmlSaxParserContext ctx =
            new HtmlSaxParserContext(context.getRuntime(), (RubyClass) klazz);
        ctx.setInputSource(context, data);
        String javaEncoding = findEncoding(context, encoding);
        if (javaEncoding != null) {
            ctx.getInputSource().setEncoding(javaEncoding);
        }
        return ctx;
    }

    /**
     * Create a new parser context that will read from a raw input
     * stream. Not a JRuby method.  Meant to be run in a separate
     * thread by XmlSaxPushParser.
     */
    public static IRubyObject parse_stream(ThreadContext context,
                                           IRubyObject klazz,
                                           InputStream stream) {
        HtmlSaxParserContext ctx =
            new HtmlSaxParserContext(context.getRuntime(), (RubyClass)klazz);
        ctx.setInputSource(stream);
        return ctx;
    }

    @Override
    protected void preParse(ThreadContext context,
                             IRubyObject handlerRuby,
                             NokogiriHandler handler) {
        // final String path = "Nokogiri::XML::FragmentHandler";
        // final String docFrag =
        //     "http://cyberneko.org/html/features/balance-tags/document-fragment";
        // RubyObjectAdapter adapter = JavaEmbedUtils.newObjectAdapter();
        // IRubyObject doc = adapter.getInstanceVariable(handlerRuby, "@document");
        // RubyModule mod =
        //     context.getRuntime().getClassFromPath(path);
        // try {
        //     if (doc != null && !doc.isNil() && adapter.isKindOf(doc, mod))
        //         parser.setFeature(docFrag, true);
        // } catch (Exception e) {
        //     // ignore
        // }
    }

}
