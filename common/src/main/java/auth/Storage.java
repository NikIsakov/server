package auth;

public interface Storage {
    public void addingUser(Object client);
//    public void removeUser(ClientHandler client);
    public boolean isNickNameOccupied(String mayBeNickname);
    public Authent getAuthenticationService();
}
