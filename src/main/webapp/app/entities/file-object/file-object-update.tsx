import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { createEntity, getEntity, reset, updateEntity } from './file-object.reducer';

export const FileObjectUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const fileObjectEntity = useAppSelector(state => state.fileObject.entity);
  const loading = useAppSelector(state => state.fileObject.loading);
  const updating = useAppSelector(state => state.fileObject.updating);
  const updateSuccess = useAppSelector(state => state.fileObject.updateSuccess);

  const handleClose = () => {
    navigate(`/file-object${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.fileSize !== undefined && typeof values.fileSize !== 'number') {
      values.fileSize = Number(values.fileSize);
    }
    if (values.durationSeconds !== undefined && typeof values.durationSeconds !== 'number') {
      values.durationSeconds = Number(values.durationSeconds);
    }
    values.createdDate = convertDateTimeToServer(values.createdDate);
    values.lastModifiedDate = convertDateTimeToServer(values.lastModifiedDate);

    const entity = {
      ...fileObjectEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          createdDate: displayDefaultDateTime(),
          lastModifiedDate: displayDefaultDateTime(),
        }
      : {
          ...fileObjectEntity,
          createdDate: convertDateTimeFromServer(fileObjectEntity.createdDate),
          lastModifiedDate: convertDateTimeFromServer(fileObjectEntity.lastModifiedDate),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="freelanceApp.fileObject.home.createOrEditLabel" data-cy="FileObjectCreateUpdateHeading">
            Create or edit a File Object
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="file-object-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Bucket"
                id="file-object-bucket"
                name="bucket"
                data-cy="bucket"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  maxLength: { value: 80, message: 'This field cannot be longer than 80 characters.' },
                }}
              />
              <ValidatedField
                label="Object Key"
                id="file-object-objectKey"
                name="objectKey"
                data-cy="objectKey"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  maxLength: { value: 255, message: 'This field cannot be longer than 255 characters.' },
                }}
              />
              <ValidatedField
                label="Content Type"
                id="file-object-contentType"
                name="contentType"
                data-cy="contentType"
                type="text"
                validate={{
                  maxLength: { value: 120, message: 'This field cannot be longer than 120 characters.' },
                }}
              />
              <ValidatedField label="File Size" id="file-object-fileSize" name="fileSize" data-cy="fileSize" type="text" />
              <ValidatedField
                label="Checksum"
                id="file-object-checksum"
                name="checksum"
                data-cy="checksum"
                type="text"
                validate={{
                  maxLength: { value: 64, message: 'This field cannot be longer than 64 characters.' },
                }}
              />
              <ValidatedField
                label="Duration Seconds"
                id="file-object-durationSeconds"
                name="durationSeconds"
                data-cy="durationSeconds"
                type="text"
              />
              <ValidatedField
                label="Created Date"
                id="file-object-createdDate"
                name="createdDate"
                data-cy="createdDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField
                label="Last Modified Date"
                id="file-object-lastModifiedDate"
                name="lastModifiedDate"
                data-cy="lastModifiedDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label="Created By"
                id="file-object-createdBy"
                name="createdBy"
                data-cy="createdBy"
                type="text"
                validate={{
                  maxLength: { value: 50, message: 'This field cannot be longer than 50 characters.' },
                }}
              />
              <ValidatedField
                label="Last Modified By"
                id="file-object-lastModifiedBy"
                name="lastModifiedBy"
                data-cy="lastModifiedBy"
                type="text"
                validate={{
                  maxLength: { value: 50, message: 'This field cannot be longer than 50 characters.' },
                }}
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/file-object" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default FileObjectUpdate;
