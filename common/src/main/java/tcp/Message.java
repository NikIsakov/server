package tcp;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "id"
)
@JsonSubTypes(
        value = {
                @JsonSubTypes.Type(value = FileMessage.class, name = "fileMessage")
        }
)
public abstract class Message {
}
