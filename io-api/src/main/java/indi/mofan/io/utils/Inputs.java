package indi.mofan.io.utils;

import indi.mofan.io.core.Input;
import indi.mofan.io.core.Output;
import indi.mofan.io.core.Receiver;
import indi.mofan.io.core.Sender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * @author mofan
 * @date 2022/10/3 18:11
 */
public class Inputs {

    static class TextInput implements Input<String, IOException> {
        final File source;
        final Reader reader;

        public TextInput(File source) throws IOException {
            this.source = source;
            reader = new FileReader(source);
        }

        @Override
        public <ReceiverThrowableType extends Throwable> void transferTo(Output<String, ReceiverThrowableType> output)
                throws IOException, ReceiverThrowableType {
            final TextSender sender = new TextSender(reader);
            output.receiveFrom(sender);

            try {
                reader.close();
            } catch (Exception e) {
                // ignore close exception :)
            }
        }

    }

    static class TextSender implements Sender<String, IOException> {
        final Reader reader;
        BufferedReader bufferReader;

        public TextSender(Reader reader) {
            this.reader = reader;
            this.bufferReader = new BufferedReader(reader);
        }

        @Override
        public <ReceiverThrowableType extends Throwable> void sendTo(Receiver<String, ReceiverThrowableType> receiver)
                throws ReceiverThrowableType, IOException {
            String readLine;
            while((readLine = bufferReader.readLine()) != null) {
                receiver.receive(readLine + "\n");
            }
        }
    }

    public static Input<String, IOException> text(File source) throws IOException {
        return new TextInput(source);
    }

    private Inputs() {}
}
