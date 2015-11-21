package org.kollavarsham;

import org.kollavarsham.utils.SpecManager;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.curriculumcourse.domain.CourseSchedule;
import org.optaplanner.examples.curriculumcourse.persistence.CurriculumCourseDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Path("/specs/{specId}")
@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.APPLICATION_JSON)
public class TimetableService {
    private static final Logger LOG = LoggerFactory
            .getLogger(TimetableService.class);

    private SolutionDao solutionDao;
    File solutionFile;
    private static ExecutorService solvingExecutor = Executors.newFixedThreadPool(4);

    @Inject org.kollavarsham.SolverForSpec solverForSpec;

    // Save the input (unsolved) XML file for this Spec into the file system
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response uploadSpecFile(
            String fileContents,
            @PathParam("specId") String specId) {
        System.out.println("SpecID is " + specId);
        LOG.info("Received Content: " + fileContents);
        String uploadedFileLocation = SpecManager.getSpecFileBasePath(specId) + "specification.xml";
        System.out.println("Upload file location is " + uploadedFileLocation);
        LOG.info("Saving file: " + uploadedFileLocation);
        try{
            // save it
            writeToFile(fileContents, uploadedFileLocation);
            String output = "File uploaded to : " + uploadedFileLocation;
            return Response.status(200).entity(output).build();
        }catch(Exception ex){
            return Response.status(500).entity(ex.getMessage()).build();
        }

    }

    // Initialize and start Solving
    @POST
    @Path("/solution")
    public Response startSolving(@PathParam("specId") String specId){

        if (!SpecManager.doesSpecFileExist(specId)){
            System.out.println("Unavailable specification");
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Unsolved solution file not found for spec: " + specId).build();
        }
        final Solver solver = solverForSpec.getSolverForSpec(specId);
        final String specIdForRunner = specId;

        System.out.println("specification available");
        if (solver != null){
            solver.terminateEarly();
        }

        solutionDao = new CurriculumCourseDao();

        File unsolvedSolutionFile = new File(SpecManager.getSpecFilePath(specId));
        final CourseSchedule unsolvedSolution = (CourseSchedule) solutionDao.readSolution(unsolvedSolutionFile);

        LOG.info("Started Solving...");

        solvingExecutor.submit(new Runnable() {
            public void run() {
                solver.solve(unsolvedSolution);

                System.out.println("Completed Solving.");
                System.out.println("Performing closing formalities.");
                //Write the best solution to an output file
                solutionFile = new File(SpecManager.getSolutionFilePath(specIdForRunner));
                if (solutionFile.getParentFile().exists() == false){
                    solutionFile.getParentFile().mkdirs();
                }
                SolutionDao finalSolutionDao = new CurriculumCourseDao();
                finalSolutionDao.writeSolution(solver.getBestSolution(), solutionFile);
            }
        });
        return Response.ok().build();
    }

    //Method to get the current solution state and score
    @GET
    @Path("/solution")
    public Response getCurrentSolution(@PathParam("specId") String specId) {
        Solver solver = solverForSpec.getSolverForSpec(specId);
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("state: " + (solver != null && solver.isSolving()));
        if (solver != null && solver.isSolving()){
            responseBuilder.append(", score: " + solver.getBestSolution().getScore().toString());
            return Response.ok().entity(responseBuilder.toString()).build();
        }else{
            responseBuilder.append(", score: " + "Score unavailable");
            return Response.ok().entity(responseBuilder.toString()).build();
        }
    }

    //terminate solving
    @DELETE
    @Path("/solution")
    public Response terminateSolving(@PathParam("specId") String specId) {
        StringBuilder responseBuilder = new StringBuilder();
        Solver solver = solverForSpec.getSolverForSpec(specId);
        if (solver != null) {
            solver.terminateEarly();
            responseBuilder.append("response: Terminated Solution");
            solverForSpec.removeSolverForSpec(specId);
            return Response.ok(responseBuilder.toString()).build();
        }
        responseBuilder.append("response: No Solution in Progress");
        return Response.ok().entity(responseBuilder.toString()).build();

    }

    //Method to get the output solved XML file
    @GET
    @Produces({ MediaType.APPLICATION_OCTET_STREAM })
    public Response getSolutionFile(@PathParam("specId") String specId){
        String filePath = SpecManager.getSolutionFilePath(specId);
        if(filePath != null && !"".equals(filePath)){
            File file = new File(filePath);
            StreamingOutput stream = null;
            try {
                final InputStream in = new FileInputStream(file);
                stream = new StreamingOutput() {
                    public void write(OutputStream out) throws IOException, WebApplicationException {
                        try {
                            int read = 0;
                            byte[] bytes = new byte[1024];

                            while ((read = in.read(bytes)) != -1) {
                                out.write(bytes, 0, read);
                            }
                        } catch (Exception e) {
                            throw new WebApplicationException(e);
                        }
                    }
                };
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return Response.ok(stream).header("content-disposition","attachment; filename = "+file.getName()).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity(null).build();
    }

    //Delete unsolved and solved files belonging to this spec along with folders
    @DELETE
    public Response removeSpecFiles(@PathParam("specId") String specId) {
        StringBuilder responseBuilder = new StringBuilder();
        if (SpecManager.removeSpecFiles(specId) == true){
            responseBuilder.append("response: " + "Terminated Solution and Removed Spec Files");
        }else{
            responseBuilder.append("response: " + "No Files to remove");
        }
        return Response.ok(responseBuilder.toString()).build();
    }

    //Utility function to save uploaded file to new location
    private void writeToFile(String contents,
                             String uploadedFileLocation) {
        try {
            OutputStream out;
            int read = 0;
            byte[] bytes = new byte[1024];

            InputStream uploadedInputStream = new ByteArrayInputStream(contents.getBytes(StandardCharsets.UTF_8));

            File fileToUpload = new File(uploadedFileLocation);
            if (fileToUpload.getParentFile().exists() == false){
                fileToUpload.getParentFile().mkdirs();
            }
            out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
