package uz.mirshodbek.dictionary_eng_ru_uz;

public class Word {
    private String eng_word;
    private String definition;

    public Word(String eng_word, String definition) {
        this.definition = definition;
        this.eng_word = eng_word;
    }

    public String getDefinition() {
        return definition;
    }

    public String get_eng_word() {
        return eng_word;
    }
}
