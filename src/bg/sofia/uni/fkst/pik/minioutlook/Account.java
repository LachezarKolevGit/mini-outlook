package bg.sofia.uni.fkst.pik.minioutlook;

public record Account(String emailAddress, String name) {

    public Account{
        if (emailAddress == null || emailAddress.isEmpty() || emailAddress.isBlank()){
            throw  new IllegalArgumentException("Email address can't be null, empty or blank");
        }
        if (name == null || name.isEmpty() || name.isBlank()){
            throw  new IllegalArgumentException("Name can't be null, empty or blank");
        }
    }
}
