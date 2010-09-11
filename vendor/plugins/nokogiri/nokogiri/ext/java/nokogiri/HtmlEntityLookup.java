package nokogiri;

import static org.jruby.javasupport.util.RuntimeHelpers.invoke;

import org.cyberneko.html.HTMLEntities;
import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyObject;
import org.jruby.anno.JRubyClass;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * @author Patrick Mahoney <pat@polycrystal.org>
 */
@JRubyClass(name="Nokogiri::HTML::EntityLookup")
public class HtmlEntityLookup extends RubyObject {

    public HtmlEntityLookup(Ruby runtime, RubyClass rubyClass) {
        super(runtime, rubyClass);
    }

    /**
     * Looks up an HTML entity <code>key</code>.
     *
     * The description is a bit lacking.
     */
    @JRubyMethod()
    public IRubyObject get(ThreadContext context, IRubyObject key) {
        Ruby ruby = context.getRuntime();
        String name = key.toString();
        int val = HTMLEntities.get(name);
        if (val == -1) return ruby.getNil();

        IRubyObject edClass =
            ruby.getClassFromPath("Nokogiri::HTML::EntityDescription");
        IRubyObject edObj = invoke(context, edClass, "new",
                                   ruby.newFixnum(val), ruby.newString(name),
                                   ruby.newString(name + " entity"));

        return edObj;
    }

}
