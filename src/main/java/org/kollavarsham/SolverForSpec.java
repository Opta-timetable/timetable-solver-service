package org.kollavarsham;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class SolverForSpec implements Serializable{

    private static final String CURRICULUM_COURSE_SOLVER_CONFIG_XML
            = "org/optaplanner/examples/curriculumcourse/solver/curriculumCourseSolverConfig.xml";

    //Store solver for each Spec
    Map<String, Solver> solvers;

    private static final Logger LOG = LoggerFactory.getLogger(SolverForSpec.class);

    public Solver getSolverForSpec(String specID) {
        //Solver is already created
        if (solvers != null && solvers.containsKey(specID)) return solvers.get(specID);
        else{
            if (solvers == null){
                solvers = new HashMap<String, Solver>();
            }
            //Create solver, store in Map and return solver
            SolverFactory solverFactory = SolverFactory.createFromXmlResource(CURRICULUM_COURSE_SOLVER_CONFIG_XML);
            Solver theSolver = solverFactory.buildSolver();
            solvers.put(specID, theSolver);
            return theSolver;
        }

    }

    public boolean removeSolverForSpec(String specId) {
        if (solvers != null){
            return (solvers.remove(specId) != null);
        }else{
            return false;
        }
    }

}
