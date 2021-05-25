package auth;

import java.util.Set;

public class Authent {
    private Set<CredentialsEntry> entries;

    public void add(String login, String password, String nickname) {
        entries.add(new CredentialsEntry(login,password,nickname));

    }

    public Authent() {
        entries = Set.of(
                new CredentialsEntry("l1", "p1","nickname1"),
                new CredentialsEntry("l2", "p2","nickname2"),
                new CredentialsEntry("l3", "p3","nickname3")
        );
    }

    public String findNicknameLoginAndPassword(String login, String password){
        for (CredentialsEntry entry: entries) {
            if (entry.getLogin().equals(login) && entry.getPassword().equals(password)){
                return entry.getNickname();
            }
        }
        return null;
    }



    public static class CredentialsEntry{
        private String login;
        private String password;
        private String nickname;

        public CredentialsEntry(String login, String password, String nickname) {
            this.login = login;
            this.password = password;
            this.nickname = nickname;
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }

        public String getNickname() {
            return nickname;
        }
    }
}
