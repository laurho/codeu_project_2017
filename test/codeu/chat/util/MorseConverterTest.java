package codeu.chat.util;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Tests the morse converter.
 *
 * @author Lauren
 *
 */
public class MorseConverterTest {

  /**
   * Tests a word converted to morse.
   */
  @Test
  public void testWordConversion() {
    String morse = MorseConverter.wordToMorse("hi");
    assertTrue(morse.equals(".... .. /"));
  }

  /**
   * Tests that more than one sentence can be translated.
   */
  @Test
  public void testSentenceConversion() {
    String paragraph = "Hi there. This is morse code. But is it? Let's check!";
    String morse = MorseConverter.paragraphToMorse(paragraph);
    assertTrue(morse.equals(".... .. /– .... . .–. . /– .... .. ... /"
        + ".. ... /–– ––– .–. ... . /–.–. ––– –.. . /"
        + "–... ..– – /.. ... /.. – /.–.. . – /... /"
        + "–.–. .... . –.–. –.– /"));

  }

  /**
   * Tests that a morse word is translated to the correct integer.
   */
  @Test
  public void testSoundTranslate() {
    String morse = ".–.–– / –//";
    List<Morse> translate = MorseConverter.morseToSoundTranslate(morse);

    List<Morse> expected = Arrays.asList(Morse.SHORTBEEP, Morse.LONGBEEP,
        Morse.SHORTBEEP, Morse.LONGBEEP, Morse.LONGBEEP, Morse.SHORTPAUSE,
        Morse.LONGPAUSE, Morse.SHORTPAUSE, Morse.LONGBEEP, Morse.LONGPAUSE,
        Morse.LONGPAUSE);
    for (int i = 0; i < translate.size(); i++) {
      assertTrue(expected.get(i).equals(translate.get(i)));
    }
  }
}
