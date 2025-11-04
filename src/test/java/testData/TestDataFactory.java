package testData;

import DTO.Createuser;
import net.datafaker.Faker;

import java.util.Locale;

public final class TestDataFactory {

    private static final Faker FAKER = new Faker(new Locale("en"));


    private TestDataFactory(){}

    public static Createuser randomUser(){
        String name=FAKER.name().firstName();
        String email=("qa"+FAKER.name().firstName()+"@test.com");
        String gender=FAKER.options().option("male","female");
        String status=FAKER.options().option("active","inactive");

        return Createuser.builder().name(name).email(email).status(status).gender(gender).build();
    }

    public static Createuser patchField(String fieldName){
        Createuser.CreateuserBuilder createuser= Createuser.builder();
        switch (fieldName.toLowerCase()){
            case "name": createuser.name(FAKER.name().firstName()); break;
            case "status": createuser.status(FAKER.options().option("active","inactive")); break;
            case "gender": createuser.gender(FAKER.options().option("male","female")); break;
            case "email": createuser.email(("qa"+FAKER.name().firstName()+"@test.com")); break;
            default: throw new IllegalArgumentException("unsupported field "+fieldName);
        }
        return createuser.build();
    }
}
