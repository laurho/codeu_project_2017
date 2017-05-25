package codeu.chat.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import com.google.common.collect.ImmutableMap;

/**
 * Morse code converter. Note that it only works with the English language and
 * can only translate the 26 letter alphabet and numbers 0-9. Note that Morse
 * code works by representing each char with a combination of "." and/or "–".
 * Words are separated by "/". There is no ending punctuation in Morse code.
 *
 * @author Lauren
 *
 */
public final class MorseConverter {
  private static final Map<Character, String> CODE = ImmutableMap
      .<Character, String>builder().put('a', ".–").put('b', "–...")
      .put('c', "–.–.").put('d', "–..").put('e', ".").put('f', "..–.")
      .put('g', "––.").put('h', "....").put('i', "..").put('j', ".–––")
      .put('k', "–.–").put('l', ".–..").put('m', "––").put('n', "–.")
      .put('o', "–––").put('p', ".––.").put('q', "––.–").put('r', ".–.")
      .put('s', "...").put('t', "–").put('u', "..–").put('v', "...–")
      .put('w', ".––").put('x', "–..–").put('y', "–.––").put('z', "––..")
      .put('0', "–––––").put('1', ".––––").put('2', "..–––")
      .put('3', "...––").put('4', "....–").put('5', ".....")
      .put('6', "–....").put('7', "––...").put('8', "–––..")
      .put('9', "––––.").build();

  /**
   * Private empty constructor for this final class.
   */
  private MorseConverter() {
  }

  /**
   * Convert a word into morse code. Only works for english alphabet.
   *
   * @param word
   *          String
   * @return String in morse
   */
  public static String wordToMorse(String word) {
    char[] characters = word.toLowerCase(Locale.ENGLISH).toCharArray();
    StringBuilder strBuild = new StringBuilder();
    for (int i = 0; i < characters.length; i++) {
      strBuild.append(CODE.get(characters[i]));
      strBuild.append(" ");
    }
    strBuild.append("/");
    return strBuild.toString();
  }

  /**
   * Converts a paragraph into morse code. Delineates all of the punctuation.
   *
   * @param paragraph
   *          String
   * @return String of morse
   */
  public static String paragraphToMorse(String paragraph) {
    String[] words = paragraph.split("\\W+");
    StringBuilder strBuild = new StringBuilder();
    for (String word : words) {
      strBuild.append(wordToMorse(word));
    }
    return strBuild.toString();
  }

  /**
   * Return the letter associated with the given morse.
   *
   * @param morse
   *          String
   * @return String of english letter
   */
  public static char morseToLetter(String morse) {
    if (!morseCheck(morse)) {
      throw new IllegalArgumentException(
          "ERROR: string of morse must be only . and –");
    }
    Iterator<Entry<Character, String>> itr = CODE.entrySet().iterator();
    while (itr.hasNext()) {
      Entry<Character, String> entry = itr.next();
      if (entry.getValue().equals(morse)) {
        return entry.getKey();
      }
    }
    return '\0';
  }

  private static boolean morseCheck(String morse) {
    for (char c : morse.toCharArray()) {
      if (c != '.' || c != '–') {
        return false;
      }
    }
    return true;
  }

  /**
   * Converts a string of morse into a string of integers that represent the
   * sounds that will be used to translate this. 0 is for dot. 1 is for dash. 2
   * is for space in between. 3 is for word ending.
   *
   * @param morse
   *          String
   * @return List of integer
   */
  public static List<Morse> morseToSoundTranslate(String morse) {
    char[] morseLetters = morse.toCharArray();
    List<Morse> soundTranslate = new ArrayList<>();
    for (char c : morseLetters) {
      switch (c) {
      case '.':
        soundTranslate.add(Morse.SHORTBEEP);
        break;
      case '–':
        soundTranslate.add(Morse.LONGBEEP);
        break;
      case ' ':
        soundTranslate.add(Morse.SHORTPAUSE);
        break;
      case '/':
        soundTranslate.add(Morse.LONGPAUSE);
      default:
        break;
      }
    }
    return soundTranslate;
  }

  /**
   * The input is assumed to be only "." and/or "–" and "/" and " ". It will
   * translate the input morse string into sound, and play it if the audio is
   * available.
   *
   * @param morse
   *          String
   */
  public static void morseToSound(String morse) {
    byte[] buf = new byte[1];
    AudioFormat af = new AudioFormat(Constants.SAMPLE_RATE,
        Constants.SAMPLE_SIZE_BITS, Constants.AUDIO_CHANNELS, true, false);
    SourceDataLine sdl;
    List<Morse> soundTranslate = MorseConverter
        .morseToSoundTranslate(morse);
    try {
      sdl = AudioSystem.getSourceDataLine(af);
      sdl.open();
      sdl.start();
      for (Morse sound : soundTranslate) {
        switch (sound) {
        case SHORTBEEP:
          // short beep "."
          for (int i = 0; i < Constants.SHORT_B_DURATION
              * Constants.SAMPLE_RATE / 1000; i++) {
            double angle = i
                / (Constants.SAMPLE_RATE / Constants.ANGLE_PARTITION) * 2.0
                * Math.PI;
            buf[0] = (byte) (Math.sin(angle) * Constants.AMPLITUDE);
            sdl.write(buf, 0, 1);
          }
          for (int i = 0; i < Constants.BEEP_BTWN_DURATION
              * Constants.SAMPLE_RATE / 1000; i++) {
            buf[0] = (byte) (0);
            sdl.write(buf, 0, 1);
          }
          break;
        case LONGBEEP:
          // long beep "–"
          for (int i = 0; i < Constants.LONG_B_DURATION
              * Constants.SOUND_ITR; i++) {
            double angle = i
                / (Constants.SAMPLE_RATE / Constants.ANGLE_PARTITION) * 2.0
                * Math.PI;
            buf[0] = (byte) (Math.sin(angle) * Constants.AMPLITUDE);
            sdl.write(buf, 0, 1);
          }
          for (int i = 0; i < Constants.BEEP_BTWN_DURATION
              * Constants.SOUND_ITR; i++) {
            buf[0] = (byte) (0);
            sdl.write(buf, 0, 1);
          }
          break;
        case SHORTPAUSE:
          // short pause " "
          for (int i = 0; i < Constants.SHORT_P_DURATION
              * Constants.SOUND_ITR; i++) {
            buf[0] = (byte) (0);
            sdl.write(buf, 0, 1);
          }
          break;
        case LONGPAUSE:
          // long pause "/"
          for (int i = 0; i < Constants.LONG_P_DURATION
              * Constants.SOUND_ITR; i++) {
            buf[0] = (byte) (0);
            sdl.write(buf, 0, 1);
          }
          break;
        default:
          break;
        }

      }
      sdl.drain();
      sdl.stop();
    } catch (LineUnavailableException e) {
      System.out.println("ERROR: the audio system could not be set up");
    }
  }
}
