import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

/**
 * @author cbongiorno on 1/31/19.
 */
class UrlExtensions {
    static ContentHandlerFactory handlerFactory = new ContentHandlerFactory() {
        @Override
        ContentHandler createContentHandler(String mimetype) {
            switch (mimetype) {
                case ~'(?:.*)?application/json.*':
                    new ContentHandler() {
                        @Override
                        Object getContent(URLConnection urlc) throws IOException {
                            new JsonSlurper().parse(urlc.errorStream ?: urlc.inputStream)
                        }
                    }
                    break
                case ~'(?:.*)?image/png.*':
                    new ContentHandler() {
                        @Override
                        Object getContent(URLConnection urlc) throws IOException {
                            new ByteArrayInputStream((urlc.errorStream ?: urlc.inputStream).bytes)
                        }
                    }
                    break
                default:
                    new ContentHandler() {
                        @Override
                        Object getContent(URLConnection urlc) throws IOException {
                            new String((urlc.errorStream ?: urlc.inputStream).bytes)
                        }
                    }
                    break
            }
        }
    }
    static {
        URLConnection.setContentHandlerFactory(handlerFactory)
    }

    static URLConnection withCreds(final URL delegate, final String u, final String p) {
        if (!(u && p))
            throw new IllegalArgumentException('Username or password not supplied')

        delegate.openConnection().tap {
            def creds = String.format('%s:%s', u, p).bytes.encodeBase64()
            setRequestProperty('Authorization', "Basic ${creds}")
        }
    }

    static Map<String, ?> get(final URLConnection delegate) {
        delegate.connect()

        // the default implementation for getting content assumes that if you have
        // an error then you have no need for the body. So, we have to handle this on our own
        // this is very implementation specific behavior
        [delegate, handlerFactory.createContentHandler(delegate.contentType)].with { conn, handler ->
            [
                    headers: conn.headerFields,
                    body   : handler?.getContent(conn as URLConnection),
                    code   : conn.responseCode
            ]
        }
    }

    static boolean exists(final URLConnection delegate) {
        delegate.setRequestMethod('HEAD')
        delegate.connect()
        delegate.responseCode in (200..299)
    }

    static Map<String, Object> delete(final URLConnection delegate) {
        delegate.setRequestMethod('DELETE')
        delegate.doOutput = true
        delegate.connect()
        [
                headers: delegate.headerFields,
                url    : delegate.url,
                code   : delegate.responseCode
        ]
    }

    static Map<String, Object> put(final URLConnection delegate,Object it) {
        delegate.setRequestMethod('PUT')
        delegate.write(it)
    }

    static Map<String, Object> write(final URLConnection delegate, Object it) {
        delegate.doOutput = true
        delegate.connect()
        switch (it) {
            case byte[]:
                delegate.outputStream.write(it as byte[])
                break
            case InputStream:
                delegate.outputStream.write((it as InputStream).bytes)
                break
            case String:
                delegate.outputStream.write((it as String).getBytes('UTF-8'))
                break
            case GString:
                delegate.outputStream.write(it.toString().getBytes('UTF-8'))
                break
            default:
                delegate.outputStream.write(new JsonBuilder(it as Object).toPrettyString().getBytes('UTF-8'))

        }

        [delegate, handlerFactory.createContentHandler(delegate.contentType)].with { conn, handler ->
            [
                    headers: conn.headerFields,
                    body   : handler?.getContent(conn as URLConnection),
                    code   : conn.responseCode
            ]
        }
    }

    static Map<String, Object> post(final URLConnection delegate, Object it) {

        delegate.setRequestMethod('POST')
        delegate.write(it)
    }

    static URLConnection json(final URLConnection delegate) {


        delegate.tap {
            setRequestProperty('Accept', 'application/json')
            setRequestProperty('Content-Type', 'application/json')
        }
    }

    static URLConnection text(final URLConnection delegate) {
        delegate.tap {
            setRequestProperty('Content-Type', 'text/plain')
            setRequestProperty('Accept', '*/*')
        }
    }

    static URLConnection accept(final URLConnection delegate) {
        delegate.tap {
            setRequestProperty('Accept', it)
        }
    }

    static URLConnection binary(final URLConnection delegate) {
        delegate.tap {
            setRequestProperty('Accept', 'application/octet-stream')
        }
    }

    static String toJson(final Map delegate) {
        new JsonBuilder(delegate).toPrettyString()
    }

    static String toJson(final Collection delegate) {
        new JsonBuilder(delegate).toPrettyString()
    }

    static Object fromJson(final InputStream delegate) {
        new JsonSlurper().parse(delegate)
    }

    static String encodeURL(final String delegate) {
        URLEncoder.encode(delegate as String, "UTF-8")
    }

    static String encodeURL(final GString delegate) {
        URLEncoder.encode(delegate.toString(), "UTF-8")
    }

    static Map<String, ?> run(final String delegate) {
        def proc = delegate.execute()
        def code = proc.waitFor()
        [
                ok      : code == 0,
                exitCode: code,
                text    : code == 0 ? proc.in.text.trim() : proc.err.text.trim(),
                cli     : delegate
        ]
    }

}
