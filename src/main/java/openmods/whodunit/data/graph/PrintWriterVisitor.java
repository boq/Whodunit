package openmods.whodunit.data.graph;

import java.io.*;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Closer;

public abstract class PrintWriterVisitor implements GraphVisitor {

    protected final PrintWriter output;

    public PrintWriterVisitor(File outputFile) {
        try {
            Closer closer = Closer.create();
            try {
                OutputStream os = closer.register(new FileOutputStream(outputFile));
                Writer w = closer.register(new OutputStreamWriter(os, Charsets.UTF_8));
                output = closer.register(new PrintWriter(w));
            } catch (Exception e) {
                closer.close();
                throw e;
            }
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void finish() {
        output.close();
    }
}
