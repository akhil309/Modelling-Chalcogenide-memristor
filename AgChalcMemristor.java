import java.io.*;

public class AgChalcMemristor extends MSSMemristor {
    private static final double TC = 0.00006;

  /** the number of MSS's */
  private static final double N = 1000000;

  private static final double W_OFF = 	0.49E-3;
  private static final double W_ON = 0.27E-3;

  /** barrier potentials */
  private static final double VA = 0.28; // lower is pointier
  private static final double VB = 0.20;

  final static double schottkeyAlpha = 1E-9; // N/A
  final static double schottkeyBeta = 0.85; // N/A


  final static double schottkeyReverseAlpha = 22E-9; // N/A
  final static double schottkeyReverseBeta = 6.2E-9; // N/A

  final static double phi = 0.92;

  /**
   * Constructor
   * 
   * @param memristance
   */
  public AgChalcMemristor(double memristance) {

    super(memristance, TC, N, W_OFF, W_ON, VA, VB, phi, schottkeyAlpha, schottkeyBeta, schottkeyReverseAlpha, schottkeyReverseBeta);
  }

}