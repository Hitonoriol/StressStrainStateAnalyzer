package hitonoriol.stressstrain.analyzer;

import com.sun.jna.Library;
import com.sun.jna.Pointer;

/*
 * Binds native Fortran library (compiled from `core-lib/stress-strain.for`) to a Java interface
 */
public interface StressStrainLibrary extends Library {

	/* Native binding to Fortran library subroutine `calc_stress_strain_state`.
	 * Parameters:
	 * 		`n`    - Number of sections
	 * 		`q1`   - External normal load intensity (even shell)
	 * 		`q2`   - External normal load intensity (odd shell)
	 * 		`kpi`  - `PI` angle divisor
	 * 		`usl1` - Left symmetry 
	 * 		`usl2` - Right symmetry
	 */
	void calcStressStrainState(int n, float q1, float q2, float kpi, int usl1, int usl2);

	/* Bindings to `zo` & `dzo` plot point getters */
	int getPlotPoints();
	Pointer getZOXPtr();
	Pointer getZOYPtr();
	Pointer getDZOXPtr();
	Pointer getDZOYPtr();
	
	/* Stress-strain state characteristics table */
	Pointer getOutTablePtr();
	int outTableSize();
	int outTableWidth();
	void outTableFree();
	
	/* Coupling errors table */
	Pointer getBiasTablePtr();
	int biasTableSize();
	int biasTableWidth();
	void biasTableFree();
	
	/* Frees all allocated memory used for calculations. */
	void dispose();
}
