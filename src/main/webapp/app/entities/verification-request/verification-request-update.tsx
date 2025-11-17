import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getProfiles } from 'app/entities/profile/profile.reducer';
import { getEntities as getFileObjects } from 'app/entities/file-object/file-object.reducer';
import { createEntity, getEntity, reset, updateEntity } from './verification-request.reducer';

export const VerificationRequestUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const profiles = useAppSelector(state => state.profile.entities);
  const fileObjects = useAppSelector(state => state.fileObject.entities);
  const verificationRequestEntity = useAppSelector(state => state.verificationRequest.entity);
  const loading = useAppSelector(state => state.verificationRequest.loading);
  const updating = useAppSelector(state => state.verificationRequest.updating);
  const updateSuccess = useAppSelector(state => state.verificationRequest.updateSuccess);

  const handleClose = () => {
    navigate(`/verification-request${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getProfiles({}));
    dispatch(getFileObjects({}));
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

    const entity = {
      ...verificationRequestEntity,
      ...values,
      profile: profiles.find(it => it.id.toString() === values.profile?.toString()),
      fileObject: fileObjects.find(it => it.id.toString() === values.fileObject?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...verificationRequestEntity,
          profile: verificationRequestEntity?.profile?.id,
          fileObject: verificationRequestEntity?.fileObject?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="freelanceApp.verificationRequest.home.createOrEditLabel" data-cy="VerificationRequestCreateUpdateHeading">
            Create or edit a Verification Request
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField name="id" required readOnly id="verification-request-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField id="verification-request-profile" name="profile" data-cy="profile" label="Profile" type="select">
                <option value="" key="0" />
                {profiles
                  ? profiles.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="verification-request-fileObject" name="fileObject" data-cy="fileObject" label="File Object" type="select">
                <option value="" key="0" />
                {fileObjects
                  ? fileObjects.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/verification-request" replace color="info">
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

export default VerificationRequestUpdate;
