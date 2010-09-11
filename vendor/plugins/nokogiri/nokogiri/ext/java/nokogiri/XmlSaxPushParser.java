package nokogiri;

import static nokogiri.internals.NokogiriHelpers.getNokogiriClass;
import static org.jruby.javasupport.util.RuntimeHelpers.invoke;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ClosedChannelException;

import nokogiri.internals.ParserContext;
import nokogiri.internals.PushInputStream;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyException;
import org.jruby.RubyObject;
import org.jruby.RubyString;
import org.jruby.anno.JRubyClass;
import org.jruby.anno.JRubyMethod;
import org.jruby.exceptions.RaiseException;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

@JRubyClass(name="Nokogiri::XML::SAX::PushParser")
public class XmlSaxPushParser extends RubyObject {
    ParserContext.Options options;
    IRubyObject optionsRuby;
    PushInputStream stream;
    Thread reader;
    Runner runner;

    public XmlSaxPushParser(Ruby ruby, RubyClass rubyClass) {
        super(ruby, rubyClass);
    }

    @JRubyMethod
    public IRubyObject initialize_native(final ThreadContext context,
                                         IRubyObject _saxParser,
                                         IRubyObject fileName) {
        optionsRuby = invoke(context,
                             context.getRuntime().getClassFromPath("Nokogiri::XML::ParseOptions"),
                             "new");
        options = new ParserContext.Options(0);
        stream = new PushInputStream();

        runner = new Runner(context, this, stream);
        reader = new Thread(runner);
        reader.start();

        return this;
    }

    /**
     * Returns an integer.
     */
    @JRubyMethod(name="options")
    public IRubyObject getOptions(ThreadContext context) {
        return invoke(context, optionsRuby, "options");
    }

    /**
     * <code>val</code> is an integer.
     */
    @JRubyMethod(name="options=")
    public IRubyObject setOptions(ThreadContext context, IRubyObject val) {
        invoke(context, optionsRuby, "options=", val);
        options =
            new ParserContext.Options(val.convertToInteger().getLongValue());
        return getOptions(context);
    }

    @JRubyMethod
    public IRubyObject native_write(ThreadContext context, IRubyObject chunk,
                                    IRubyObject isLast) {
        byte[] data = null;
        if (chunk instanceof RubyString || chunk.respondsTo("to_str")) {
            data = chunk.convertToString().getBytes();
        } else {
            throw new RaiseException(new XmlSyntaxError(context.getRuntime()));
        }

        int errorCount0 = runner.getErrorCount();

        try {
            stream.writeAndWaitForRead(data);
        } catch (ClosedChannelException e) {
            // ignore
        } catch (IOException e) {
            throw context.getRuntime().newRuntimeError(e.toString());
        }

        if (isLast.isTrue()) {
            try {
                stream.close();
            } catch (IOException e) {
                // ignore
            }

            for (;;) {
                try {
                    reader.join();
                    break;
                } catch (InterruptedException e) {
                    // continue loop
                }
            }
        }

        if (!options.recover && runner.getErrorCount() > errorCount0) {
            throw new RaiseException(runner.getLastError(), true);
        }

        return this;
    }

    protected static class Runner implements Runnable {
        protected ThreadContext context;
        protected IRubyObject handler;
        protected XmlSaxParserContext parser;

        public Runner(ThreadContext context,
                      IRubyObject handler,
                      InputStream stream) {
            RubyClass klazz = getNokogiriClass(context.getRuntime(), "Nokogiri::XML::SAX::ParserContext");

            this.context = context;
            this.handler = handler;
            this.parser = (XmlSaxParserContext)
                XmlSaxParserContext.parse_stream(context, klazz, stream);
        }

        public void run() {
            parser.parse_with(context, handler);
        }

        public int getErrorCount() {
            // check for null because thread may nto have started yet
            if (parser.getNokogiriHandler() == null) return 0;
            else return parser.getNokogiriHandler().getErrorCount();
        }

        public RubyException getLastError() {
            return (RubyException) parser.getNokogiriHandler().getLastError();
        }
    }
}
