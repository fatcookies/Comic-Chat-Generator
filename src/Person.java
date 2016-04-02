/**
 * Created by adam on 01/04/2016.
 */
public class Person {

    private String nick;
    private Character character;

    public Person(String nick) {
        this.nick = nick;
    }

    public String getNick() {
        return nick;
    }

    public void assignCharacter(Character character) {
        this.character = character;
    }

    public Character getCharacter() {
        return character;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        return nick.equals(person.nick);
    }

    @Override
    public int hashCode() {
        return nick.hashCode();
    }
}
