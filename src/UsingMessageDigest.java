public class UsingMessageDigest{
    public static void main(String[] args) {
        String text = "I like apple";
        System.out.println("MD5 of "+ text +":"+MD5Creator.getMD5(text));

        String moddfyedText = text.replace(" ","");

        System.out.println("MD5 of "+ moddfyedText + ":"+MD5Creator.getMD5(moddfyedText));
    }
}