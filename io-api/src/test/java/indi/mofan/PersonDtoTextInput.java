package indi.mofan;

import indi.mofan.dto.PersonDto;
import indi.mofan.io.core.Input;
import indi.mofan.io.core.Output;

import java.io.IOException;

/**
 * @author mofan
 * @date 2022/10/4 14:37
 */
public class PersonDtoTextInput implements Input<String, IOException> {
    private PersonDto personDto;

    public PersonDtoTextInput(PersonDto personDto) {
        this.personDto = personDto;
    }

    @Override
    public <ReceiverThrowableType extends Throwable> void transferTo(Output<String, ReceiverThrowableType> output)
            throws IOException, ReceiverThrowableType {
        final PersonDtoTextSender sender = new PersonDtoTextSender(personDto);
        output.receiveFrom(sender);
    }
}
