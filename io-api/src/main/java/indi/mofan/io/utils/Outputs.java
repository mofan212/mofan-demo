package indi.mofan.io.utils;

import indi.mofan.io.core.Output;
import indi.mofan.io.core.Receiver;
import indi.mofan.io.core.Sender;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * @author mofan
 * @date 2022/10/3 18:12
 */
public class Outputs {
    static class TextOutput implements Output<String, IOException> {
        final File destination;
        final Writer writer;

        public TextOutput(File destination) throws IOException {
            this.destination = destination;
            writer = new FileWriter(destination);
        }

        @Override
        public <SenderThrowableType extends Throwable> void receiveFrom(Sender<String, SenderThrowableType> sender)
                throws IOException, SenderThrowableType {
            final TextFileReceiver receiver = new TextFileReceiver(writer);
            sender.sendTo(receiver);
            receiver.finished();

            try {
                writer.close();
            } catch (Exception e) {
                // ignore close exception
            }
        }
    }

    record TextFileReceiver(Writer writer) implements Receiver<String, IOException> {

        @Override
            public void receive(String item) throws IOException {
                writer.write(item);
            }

            @Override
            public void finished() {
            }
        }

    public static Output<String, IOException> text(File destination) throws IOException {
        return new TextOutput(destination);
    }

    private Outputs() {
    }
}
