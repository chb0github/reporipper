#!/usr/bin/env groovy
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import static java.lang.System.err
import static java.lang.System.exit
import cmds.*

def handlerFactory = new ContentHandlerFactory() {
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
URLConnection.setContentHandlerFactory(handlerFactory)



URL.metaClass.withCreds = { u, p ->
    delegate.openConnection().tap {
        setRequestProperty('Authorization', "Basic ${(u + ':' + p).bytes.encodeBase64()}")
    }
}

URLConnection.metaClass.get = {
    delegate.connect()

    // the default implementation for getting content assumes that if you have
    // an error then you have no need for the body. So, we have to handle this on our own
    // this is very implementation specific behavior
    [delegate,handlerFactory.createContentHandler(delegate.contentType)].with {conn, handler ->
        [
                headers: conn.headerFields,
                body   : handler?.getContent(conn as URLConnection),
                code   : conn.responseCode
        ]
    }
}

URLConnection.metaClass.exists = {
    delegate.setRequestMethod('HEAD')
    delegate.connect()
    delegate.responseCode in (200..299)
}
URLConnection.metaClass.delete = {
    delegate.setRequestMethod('DELETE')
    delegate.doOutput = true
    delegate.connect()
    [
            headers: delegate.headerFields,
            url   : delegate.url,
            code   : delegate.responseCode
    ]
}
URLConnection.metaClass.put = {
    delegate.setRequestMethod('PUT')
    delegate.write(it)
}
URLConnection.metaClass.write = {
    delegate.doOutput = true
    delegate.connect()
    switch (it){
        case byte[]:
            delegate.outputStream.write(it)
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

    [delegate,handlerFactory.createContentHandler(delegate.contentType)].with {conn, handler ->
        [
                headers: conn.headerFields,
                body   : handler?.getContent(conn as URLConnection),
                code   : conn.responseCode
        ]
    }
}
URLConnection.metaClass.post = {
    delegate.setRequestMethod('POST')
    delegate.write(it)
}
URLConnection.metaClass.json = {
    delegate.tap {
        setRequestProperty('Accept', 'application/json')
        setRequestProperty('Content-Type', 'application/json')
    }
}
URLConnection.metaClass.text = {
    delegate.tap {
        setRequestProperty('Content-Type', 'text/plain')
        setRequestProperty('Accept', '*/*')
    }
}
URLConnection.metaClass.accept = {
    delegate.tap {
        setRequestProperty('Accept', it)
    }
}


URLConnection.metaClass.binary = {
    delegate.tap {
        setRequestProperty('Accept', 'application/octet-stream')
    }
}
Map.metaClass.toJson = {
    new JsonBuilder(delegate).toPrettyString()
}
Collection.metaClass.toJson = {
    new JsonBuilder(delegate).toPrettyString()
}

InputStream.metaClass.fromJson = {
    new JsonSlurper().parse(delegate)
}

GString.metaClass.run = {
    def code = proc.waitFor()
    [
            ok      : code == 0,
            exitCode: code,
            text    : code == 0 ? proc.in.text.trim() : proc.err.text.trim(),
            cli     : delegate.toString()
    ]

}
GString.metaClass.encodeURL = {
    URLEncoder.encode(delegate.toString(), "UTF-8")
}
List.metaClass.run = {
    def proc = delegate.execute()
    proc.waitForOrKill(5000)
    def text = {
        try {
            if (proc.exitcode == 0) {
                if (proc.in.available() > 0)
                    return proc.in.text.trim()
            } else {
                if (proc.err.available() > 0)
                    return proc.err.text.trim()
            }
        } catch (IOException e) {
            return null
        }
    }()

    [
            ok      : proc.hasExited && proc.exitcode == 0,
            exitCode: proc.hasExited ? proc.exitcode : null,
            text    : text,
            cli     : delegate.join(' ')
    ]
}
String.metaClass.encodeURL = {
    URLEncoder.encode(delegate as String, "UTF-8")
}
String.metaClass.run = {
    def proc = delegate.execute()
    def code = proc.waitFor()
    [
            ok      : code == 0,
            exitCode: code,
            text    : code == 0 ? proc.in.text.trim() : proc.err.text.trim(),
            cli     : delegate
    ]
}



def config = new JsonSlurper().parse(new File("${System.properties['user.dir']}/.scm.json"))

def cmd = args[0]
args = args.drop(1)

def opts = (args - args.findAll { it =~ '--.*' }).split { it.startsWith('-') }.transpose().collectEntries()
args = args - opts.keySet() - opts.values()
def chosenSwitches = args.findAll { it =~ '--.*' } as Set
args = args - chosenSwitches


Command command = ServiceLoader.load(Command.class).find { it.name == cmd }


if (command) {
    def args = binding.variables.remove('args') as String[]
    def format = opts.remove('-format')?.with { format -> evaluate(format as String) } ?: { it.toJson() }
    function = command.&execute >> format >> this.&println
    function(new Context(args, opts, chosenSwitches, config))

} else {
    err.println("Unknown " + "command ${cmd}") || exit(-1)
}