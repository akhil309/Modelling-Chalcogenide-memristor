import java.util.Random; 
public abstract class MSSMemristor {

    // public static void main(String[] args){}

   
    private final static	Random	RANDOM	=	new	Random();
    //	CONSTANTS
    /**	Boltzman's	constant	*/
    private static final double	K	=	1.3806503E-23;
    /**	electron	charge	*/
    private static final double	Q	=	1.60217646E-19;
    /**	temperature	in	Kelvin	*/
    private static double	TEMP	=	298.0;
    private static double	BETA	=	Q	/	(K	*	TEMP);
    /**	thermal	voltage	*/
    private static double	VT	=	1.0	/	BETA;
    //	DEVICE	PARAMETERS
    /**	characteristic	time	scale	of	the	device	*/
    private final double	tc;
    /**	the	number	of	MSS's	*/
    private final double	n;
    /**	conductance	contributed	from	each	MSS	*/
    private final double	Ga;
    private final double	Gb;
    /**	between	0	and	1,	what	percentage	of	the	current	comes	from	the	MSS	model?	the	rest	comes	from
    schottkey	barrier	exponential	current	*/
    private final double	phi;
    private final double	schottkeyAlpha;
    private final double	schottkeyBeta;
    private final double	schottkeyReverseAlpha;
    private final double	schottkeyReverseBeta;
    /**	barrier	potentials	*/
    private final double	vA;
    private final double	vB;
    //	DEVICE	DYNAMIC	VARIABLES
    /**
    *	Nb	is	the	number	of	MSS's	in	the	B	state,
    *	the	state	of	the	memristor,	contained	in	this	one	variable
    */
    private double	Nb;
    /**
    *	Constructor
    */
    public MSSMemristor(double	memristance,	double	tc,	double	n,	double	gOff,	double	gOn,	double	vA,	double
    vB,	double	phi,	double	schottkeyAlpha,	double	schottkeyBeta,	double	schottkeyReverseAlpha,
    double	schottkeyReverseBeta) {
    if	(memristance	>	1.0	||	memristance	<	0.0)	{
    throw new	IllegalArgumentException("Memristance	must	be	between	0	and	1,	inclusive!!!");
    }
    //	init	the	device	in	a	certain	state
    Nb	=	(1	-	memristance)	*	n;	//	note:	(1-	memristance)	so	that	a	memristance	of	1	give	a	higherresistance	than	memristance	of	0.
    this.tc	=	tc;
    this.n	=	n;
    this.Gb	=	gOff/n;
    this.Ga	=	gOn/n;
    this.vA	=	vA;
    this.vB	=	vB;
    this.phi	=	phi;
    this.schottkeyAlpha	=	schottkeyAlpha;
    this.schottkeyBeta	=	schottkeyBeta;
    this.schottkeyReverseAlpha	=	schottkeyReverseAlpha;
    this.schottkeyReverseBeta	=	schottkeyReverseBeta;
    }
    /**
    *	update	device	conductance
    *
    *	@param	voltage	-	the	instantaneous	voltage
    *	@param	dt	-	how	much	time	passed	since	the	last	update
    */
    public void dG(double	voltage,	double	dt) {
    //	Probabilities
    double	Pa	=	Pa(voltage,	dt);
    double	Pb	=	Pb(voltage,	dt);
    //	Gaussian	mean
    double	u_a	=	(n	-	Nb)	*	Pa;	//	Na	*	Pa
    double	u_b	=	(Nb)	*	Pb;
    //	Gaussian	standard	deviation
    double	stdv_a	=	Math.sqrt((n	-	Nb)	*	Pa	*	(1	-	Pa));
    double	stdv_b	=	Math.sqrt((Nb)	*	Pb	*	(1	-	Pb));
    //	Number	of	switches	making	a	transistion
    double	Nab	=	Math.round(normal(u_a,	stdv_a));
    double	Nba	=	Math.round(normal(u_b,	stdv_b));
    //	update	the	state	of	the	memristor,	contained	in	this	one	variable
    Nb	+=	(Nab	-	Nba);
    if	(Nb	>	n)	{
    Nb	=	n;
    }
    else if	(Nb	<	0)	{
    Nb	=	0;
    }
    }
    /**
    *	Equation	21.	The	probability	that	the	MSS	will	transition	from	the	A	state	to	the	B	state
    *
    *	@param	voltage	-	the	voltage	across	the	device
    *	@param	dt
    *	@return
    */
    public double Pa(double	v,	double	dt) {
    double	exponent	=	-1	*	(v	-	vA)	/	VT;
    double	alpha	=	dt	/	tc;
    double	Pa	=	alpha	/	(1.0	+	Math.exp(exponent));
    if	(Pa	>	1.0)	{
    Pa	=	1.0;
    }
    else if	(Pa	<	0.0)	{
    Pa	=	0.0;
    }
    return	Pa;
    }
    /**
    *	Equation	22.	The	probability	that	the	MSS	will	transition	from	the	B	state	to	the	A	state
    *
    *	@param	v
    *	@param	dt
    *	@return
    */
    public double Pb(double	v,	double	dt) {
    double	exponent	=	-1	*	(v	+	vB)	/	VT;
    double	alpha	=	dt	/	tc;
    double	Pb	=	alpha	*	(1.0	-	1.0	/	(1.0	+	Math.exp(exponent)));
    if	(Pb	>	1.0)	{
    Pb	=	1.0;
    }
    else if	(Pb	<	0.0)	{
    Pb	=	0.0;
    }
    return	Pb;
    }
    /**
    *	Gaussian/	normal	distribnution
    *	
    *	@param	u
    *	@param	stdv
    *	@return
    */
    private double normal(double	u,	double	stdv) {
    return	stdv	*	RANDOM.nextGaussian()	+	u;
    }
    /**
    *	Equation	23.	all	variables	are	constant	except	Nb
    *	
    *	@return
    */
    public double getConductance() {
    return	Nb	*	(Gb	-	Ga)	+	n	*	Ga;
    }
    public double getResistance() {
    return 1.0	/	getConductance();
    }
    /**
    *	Get	the	current	thru	this	memristor
    *
    *	@return	the	combined	MSS	and	Schottkey	current
    */
    public double getCurrent(double	voltage) {
    double	mssCurrent	=	voltage	*	getConductance();
    double	schottkeyCurrent	=	getSchottkeyCurrent(voltage);
    return	phi	*	mssCurrent	+	(1	-	phi)	*	schottkeyCurrent;
    }
    public double getSchottkeyCurrent(double	voltage) {
    return	schottkeyReverseAlpha	*	(-1	*	Math.exp(-1	*	schottkeyReverseBeta	*	voltage))	+	schottkeyAlpha	*
    (Math.exp(schottkeyBeta	*	voltage));
    }
    public double getSchottkeyCurrentWithPhi(double	voltage) {
    return	(1	-	phi)	*	schottkeyReverseAlpha	*	(-1	*	Math.exp(-1	*	schottkeyReverseBeta	*	voltage))	+
    schottkeyAlpha	*	(Math.exp(schottkeyBeta	*	voltage));
    }
    public void setTemperature(double	temperatureInKelvin) {
    TEMP	=	temperatureInKelvin;
    BETA	=	Q	/	(K	*	TEMP);
    VT	=	1.0	/	BETA;
    }

}