package com.ft.methodeapi.service.methode;

import EOM.FileSystemAdmin;
import EOM.FileSystemObject;
import EOM.InvalidURI;
import EOM.ObjectNotFound;
import EOM.PermissionDenied;
import EOM.Repository;
import EOM.RepositoryError;
import EOM.Session;
import com.ft.methodeapi.model.EomFile;
import com.ft.methodeapi.service.methode.templates.MethodeRepositoryOperationTemplate;
import com.ft.methodeapi.service.methode.templates.MethodeSessionOperationTemplate;
import com.google.common.base.Optional;
import com.yammer.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodeFileRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodeFileRepository.class);
    private final MethodeObjectFactory client;


    public MethodeFileRepository(MethodeObjectFactory client) {
        this.client = client;
    }

    @Timed
    public void ping() {
        new MethodeRepositoryOperationTemplate<>(client).doOperation(new MethodeRepositoryOperationTemplate.RepositoryCallback<Object>() {
            @Override
            public Object doOperation(Repository repository) {
                repository.ping();
                return null;
            }
        });
    }

    @Timed
    public Optional<EomFile> findFileByUuid(final String uuid) {

        final MethodeSessionOperationTemplate<Optional<EomFile>> template = new MethodeSessionOperationTemplate<>(client);

        MethodeSessionOperationTemplate.SessionCallback<Optional<EomFile>> callback;

        callback=new MethodeSessionOperationTemplate.SessionCallback<Optional<EomFile>>() {
            @Override
            public Optional<EomFile> doOperation(Session session, Repository repository) {
                FileSystemAdmin fileSystemAdmin;
                try {
                    fileSystemAdmin = EOM.FileSystemAdminHelper.narrow(session.resolve_initial_references("FileSystemAdmin"));
                } catch (ObjectNotFound | RepositoryError | PermissionDenied e) {
                    throw new MethodeException(e);
                }

                String uri = "eom:/uuids/" + uuid;

                FileSystemObject fso;
                Optional<EomFile> foundContent;
                try {
                    fso = fileSystemAdmin.get_object_with_uri(uri);

                    EOM.File eomFile = EOM.FileHelper.narrow(fso);

                    final String typeName = eomFile.get_type_name();
                    final byte[] bytes = eomFile.read_all();
                    final String attributes = eomFile.get_attributes();
                    EomFile content = new EomFile(uuid, typeName, bytes, attributes);
                    foundContent = Optional.of(content);

                    eomFile._release();
                    fileSystemAdmin._release();

                } catch (InvalidURI invalidURI) {
                    return Optional.absent();
                } catch (RepositoryError | PermissionDenied e) {
                    throw new MethodeException(e);
                }
                return foundContent;
            }
        };

       return template.doOperation(callback);
    }


}
