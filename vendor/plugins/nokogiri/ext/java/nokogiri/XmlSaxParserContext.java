package nokogiri;

import static nokogiri.internals.NokogiriHelpers.rubyStringToString;
import static org.jruby.javasupport.util.RuntimeHelpers.invoke;

import java.io.IOException;
import java.io.InputStream;

import nokogiri.internals.NokogiriHandler;
import nokogiri.internals.ParserContext;
import nokogiri.internals.XmlSaxParser;

import org.apache.xerces.parsers.AbstractSAXParser;
import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyModule;
import org.jruby.RubyObjectAdapter;
import org.jruby.anno.JRubyClass;
import org.jruby.anno.JRubyMethod;
import org.jruby.exceptions.RaiseException;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

/**
 * Base class for the SAX parsers.
 *
 * @author Patrick Mahoney <pat@polycrystal.org>
 */
@JRubyClass(name="Nokogiri::XML::SAX::ParserContext")
public class XmlSaxParserContext extends ParserContext {
    protected static final String FEATURE_NAMESPACES =
        "http://xml.org/sax/features/namespaces";
    protected static final String FEATURE_NAMESPACE_PREFIXES =
        "http://xml.org/sax/features/namespace-prefixes";
    protected static final String FEATURE_LOAD_EXTERNAL_DTD =
        "http://apache.org/xml/features/nonvalidating/load-external-dtd";

    protected AbstractSAXParser parser;

    protected NokogiriHandler handler = null;

    public XmlSaxParserContext(final Ruby ruby, RubyClass rubyClass) {
        super(ruby, rubyClass);
        try {
            parser = createParser();
        } catch (SAXException se) {
            throw RaiseException.createNativeRaiseException(ruby, se);
        }
    }

    protected AbstractSAXParser createParser() throws SAXException {
        XmlSaxParser parser = new XmlSaxParser();
        parser.setFeature(FEATURE_NAMESPACE_PREFIXES, true);
        parser.setFeature(FEATURE_LOAD_EXTERNAL_DTD, false);
        return parser;
    }

    /**
     * Create a new parser context that will parse the string
     * <code>data</code>.
     */
    @JRubyMethod(name="memory", meta=true)
    public static IRubyObject parse_memory(ThreadContext context,
                                           IRubyObject klazz,
                                           IRubyObject data) {
        XmlSaxParserContext ctx = new XmlSaxParserContext(context.getRuntime(),
                                                          (RubyClass) klazz);
        ctx.setInputSource(context, data);
        return ctx;
    }

    /**
     * Create a new parser context that will read from the file
     * <code>data</code> and parse.
     */
    @JRubyMethod(name="file", meta=true)
    public static IRubyObject parse_file(ThreadContext context,
                                         IRubyObject klazz,
                                         IRubyObject data) {
        XmlSaxParserContext ctx = new XmlSaxParserContext(context.getRuntime(),
                                                          (RubyClass) klazz);
        ctx.setInputSourceFile(context, data);
        return ctx;
    }

    /**
     * Create a new parser context that will read from the IO or
     * StringIO <code>data</code> and parse.
     *
     * TODO: Currently ignores encoding <code>enc</code>.
     */
    @JRubyMethod(name="io", meta=true)
    public static IRubyObject parse_io(ThreadContext context,
                                       IRubyObject klazz,
                                       IRubyObject data,
                                       IRubyObject enc) {
        //int encoding = (int)enc.convertToInteger().getLongValue();
        XmlSaxParserContext ctx = new XmlSaxParserContext(context.getRuntime(),
                                                          (RubyClass) klazz);
        ctx.setInputSource(context, data);
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
        XmlSaxParserContext ctx =
            new XmlSaxParserContext(context.getRuntime(), (RubyClass)klazz);
        ctx.setInputSource(stream);
        return ctx;
    }

    /**
     * Set a property of the underlying parser.
     */
    protected void setProperty(String key, Object val)
        throws SAXNotRecognizedException, SAXNotSupportedException {
        parser.setProperty(key, val);
    }

    protected void setContentHandler(ContentHandler handler) {
        parser.setContentHandler(handler);
    }

    protected void setErrorHandler(ErrorHandler handler) {
        parser.setErrorHandler(handler);
    }

    public NokogiriHandler getNokogiriHandler() {
        return handler;
    }

    /**
     * Perform any initialization prior to parsing with the handler
     * <code>handlerRuby</code>. Convenience hook for subclasses.
     */
    protected void preParse(ThreadContext context,
                            IRubyObject handlerRuby,
                            NokogiriHandler handler) {
        ((XmlSaxParser) parser).setXmlDeclHandler(handler);
    }

    protected void postParse(ThreadContext context,
                             IRubyObject handlerRuby,
                             NokogiriHandler handler) {
        // noop
    }

    protected void do_parse() throws SAXException, IOException {
        parser.parse(getInputSource());
    }

    @JRubyMethod
    public IRubyObject parse_with(ThreadContext context,
                                  IRubyObject handlerRuby) {
        Ruby ruby = context.getRuntime();

        if(!invoke(context, handlerRuby, "respond_to?",
                   ruby.newSymbol("document")).isTrue()) {
            String msg = "argument must respond_to document";
            throw ruby.newArgumentError(msg);
        }

        handler = new NokogiriHandler(ruby, handlerRuby);
        preParse(context, handlerRuby, handler);

        setContentHandler(handler);
        setErrorHandler(handler);

        try{
            setProperty("http://xml.org/sax/properties/lexical-handler",
                        handler);
        } catch(Exception ex) {
            throw ruby.newRuntimeError(
                "Problem while creating XML SAX Parser: " + ex.toString());
        }

        try{
            try {
                do_parse();
            } catch(SAXParseException spe) {
                // A bad document (<foo><bar></foo>) should call the
                // error handler instead of raising a SAX exception.

                // However, an EMPTY document should raise a
                // RuntimeError.  This is a bit kludgy, but AFAIK SAX
                // doesn't distinguish between empty and bad whereas
                // Nokogiri does.
                String message = spe.getMessage();
                if ("Premature end of file.".matches(message)) {
                    throw ruby.newRuntimeError(
                        "couldn't parse document: " + message);
                } else {
                    handler.error(spe);
                }

            }
        } catch(SAXException se) {
            throw RaiseException.createNativeRaiseException(ruby, se);
        } catch(IOException ioe) {
            throw ruby.newIOErrorFromException(ioe);
        }

        postParse(context, handlerRuby, handler);

        //maybeTrimLeadingAndTrailingWhitespace(context, handlerRuby);

        return ruby.getNil();
    }

    /**
     * Can take a boolean assignment.
     *
     * @param context
     * @param value
     * @return
     */
    @JRubyMethod(name = "replace_entities=")
    public IRubyObject set_replace_entities(ThreadContext context,
                                            IRubyObject value) {
        if (!value.isTrue()) {
            throw context.getRuntime()
                .newRuntimeError("Not replacing entities is unsupported");
        }

        return this;
    }

    @JRubyMethod(name="replace_entities")
    public IRubyObject get_replace_entities(ThreadContext context,
                                            IRubyObject value) {
        return context.getRuntime().getTrue();
    }


    /**
     * If the handler's document is a FragmentHandler, attempt to trim
     * leading and trailing whitespace.
     *
     * This is a bit hackish and depends heavily on the internals of
     * FragmentHandler.
     */
    protected void maybeTrimLeadingAndTrailingWhitespace(ThreadContext context,
                                                         IRubyObject parser) {
        final String path = "Nokogiri::XML::FragmentHandler";
        RubyObjectAdapter adapter = JavaEmbedUtils.newObjectAdapter();
        RubyModule mod =
            context.getRuntime().getClassFromPath(path);

        IRubyObject handler = adapter.getInstanceVariable(parser, "@document");
        if (handler == null || handler.isNil() || !adapter.isKindOf(handler, mod))
            return;
        IRubyObject stack = adapter.getInstanceVariable(handler, "@stack");
        if (stack == null || stack.isNil())
            return;
        // doc is finally a DocumentFragment whose nodes we can check
        IRubyObject doc = adapter.callMethod(stack, "first");
        if (doc == null || doc.isNil())
            return;

        IRubyObject children;

        for (;;) {
            children = adapter.callMethod(doc, "children");
            IRubyObject first = adapter.callMethod(children, "first");
            if (isWhitespaceText(context, first))
                adapter.callMethod(first, "unlink");
            else
                break;
        }

        for (;;) {
            children = adapter.callMethod(doc, "children");
            IRubyObject last = adapter.callMethod(children, "last");
            if (isWhitespaceText(context, last))
                adapter.callMethod(last, "unlink");
            else
                break;
        }

        // While we have a document, normalize it.
        ((XmlNode) doc).normalize();
    }

    protected boolean isWhitespaceText(ThreadContext context, IRubyObject obj) {
        if (obj == null || obj.isNil()) return false;

        XmlNode node = (XmlNode) obj;
        if (!(node instanceof XmlText))
            return false;

        String content = rubyStringToString(node.content(context));
        return content.trim().length() == 0;
    }

}
