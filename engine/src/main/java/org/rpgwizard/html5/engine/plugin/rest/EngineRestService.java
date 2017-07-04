/**
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.html5.engine.plugin.rest;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.rpgwizard.html5.engine.plugin.rest.model.FileSave;

/**
 *
 * @author Joshua Michael Daly
 */
@Path("/engine")
public class EngineRestService {

	private final File projectPath;

	public EngineRestService(String projectPath) {
		this.projectPath = new File(projectPath);
	}

	@POST
	@Path("/load")
        @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response load(String json) {
            try {
                JSONObject object = new JSONObject(json);
                File targetPath = new File(
                        projectPath.getAbsoluteFile() 
                        + File.separator
                        + object.getString("path")
                );
                
                String entity = FileUtils.readFileToString(targetPath, "UTF-8");
                return Response.status(Status.OK).entity(entity).build();
            } catch (IOException | JSONException ex) {
                Logger.getLogger(EngineRestService.class.getName()).log(Level.SEVERE, null, ex);
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }	
	}
	@POST
	@Path("/save")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response save(String json) {
            try {
                JSONObject object = new JSONObject(json);
                File targetPath = new File(
                        projectPath.getAbsoluteFile() 
                        + File.separator
                        + object.getString("path")
                );
                
                switch (object.getString("type").toLowerCase()) {
                    case "board":
                    default:
                        FileUtils.writeStringToFile(targetPath, object.getJSONObject("data").toString(), false);
                }
                
                return Response.status(Status.OK).build();
            } catch (IOException | JSONException ex) {
                Logger.getLogger(EngineRestService.class.getName()).log(Level.SEVERE, null, ex);
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }	
	}
}
