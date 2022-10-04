package indi.mofan;

import indi.mofan.dto.PersonDto;
import indi.mofan.io.core.Receiver;
import indi.mofan.io.core.Sender;

import java.io.IOException;

/**
 * @author mofan
 * @date 2022/10/4 14:38
 */
public class PersonDtoTextSender implements Sender<String, IOException> {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private PersonDto personDto;

    public PersonDtoTextSender(PersonDto personDto) {
        this.personDto = personDto;
    }

    @Override
    public <ReceiverThrowableType extends Throwable> void sendTo(Receiver<String, ReceiverThrowableType> receiver)
            throws ReceiverThrowableType {
        receiver.receive("name: " + personDto.getName() + LINE_SEPARATOR);
        receiver.receive("age: " + personDto.getAge() + LINE_SEPARATOR);
    }
}
