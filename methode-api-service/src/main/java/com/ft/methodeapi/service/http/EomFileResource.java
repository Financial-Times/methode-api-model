package com.ft.methodeapi.service.http;

import static java.lang.String.format;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.ft.api.jaxrs.errors.ClientError;
import com.ft.api.jaxrs.errors.ServerError;
import com.ft.methodeapi.model.EomFile;
import com.ft.methodeapi.service.methode.ActionNotPermittedException;
import com.ft.methodeapi.service.methode.InvalidEomFileException;
import com.ft.methodeapi.service.methode.MethodeException;
import com.ft.methodeapi.service.methode.MethodeFileRepository;
import com.ft.methodeapi.service.methode.NotFoundException;
import com.google.common.base.Optional;
import com.yammer.metrics.annotation.Timed;
import org.omg.CORBA.SystemException;

@Path("eom-file")
public class EomFileResource {

    private static final String CHARSET_UTF_8 = ";charset=utf-8";

    private final MethodeFileRepository methodeContentRepository;

    public EomFileResource(MethodeFileRepository methodeContentRepository) {
        this.methodeContentRepository = methodeContentRepository;
    }

    @GET
    @Timed
    @Path("/{uuid}")
    @Produces(MediaType.APPLICATION_JSON + CHARSET_UTF_8)
    public Optional<EomFile> getByUuid(@PathParam("uuid") String uuid) {
        try {
            return methodeContentRepository.findFileByUuid(uuid);
        } catch(MethodeException | SystemException ex) {
            throw ServerError.status(503).error("error accessing upstream system").exception(ex);
        }
    }

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public EomFile newTestFile(final EomFile eomFile) {
        final String filename = "test-file-" + System.currentTimeMillis() + ".xml";
        try {
            EomFile newEomFile = methodeContentRepository.createNewTestFile(filename, eomFile);
            return newEomFile;
        } catch (InvalidEomFileException e) {
            throw ClientError.status(422).exception(e);
        }
    }

    @DELETE
    @Timed
    @Path("/{uuid}")
    public void deleteByUuid(@PathParam("uuid") String uuid) {
        try {
            methodeContentRepository.deleteTestFileByUuid(uuid);
        } catch (NotFoundException e) {
            throw ClientError.status(404).error(format("%s not found", uuid)).exception();
        } catch (ActionNotPermittedException e) {
            throw ClientError.status(403).error(format("not allowed to delete %s", uuid)).exception(e);
        } catch (MethodeException | SystemException e) {
            throw ServerError.status(503).error("error accessing upstream system").exception(e);
        }
    }

}
