package codeu.chat.util;

/**
 * Public static final constants to use throughout the program.
 *
 * @author Lauren
 *
 */
public class Constants {
  // MorseConverter
  public static final float SAMPLE_RATE = 8000F; // sample of audio per second
  public static final int SAMPLE_SIZE_BITS = 8; // size of the audio
  public static final int AUDIO_CHANNELS = 1; // mono,1 vs stereo,2
  public static final int SOUND_ITR = (int) (SAMPLE_RATE / 1000);
  public static final int SHORT_B_DURATION = 80;
  public static final int LONG_B_DURATION = 200;
  public static final int ANGLE_PARTITION = 800; // divisor for ~10 groups of
                                                 // sample rate
  public static final int SHORT_P_DURATION = 100;
  public static final int LONG_P_DURATION = 700;
  public static final int BEEP_BTWN_DURATION = 100;
  public static final int AMPLITUDE = 100;

}
