import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getOffers } from 'app/entities/offer/offer.reducer';
import { getEntities as getFileObjects } from 'app/entities/file-object/file-object.reducer';
import { MediaKind } from 'app/shared/model/enumerations/media-kind.model';
import { createEntity, getEntity, reset, updateEntity } from './offer-media.reducer';

export const OfferMediaUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const offers = useAppSelector(state => state.offer.entities);
  const fileObjects = useAppSelector(state => state.fileObject.entities);
  const offerMediaEntity = useAppSelector(state => state.offerMedia.entity);
  const loading = useAppSelector(state => state.offerMedia.loading);
  const updating = useAppSelector(state => state.offerMedia.updating);
  const updateSuccess = useAppSelector(state => state.offerMedia.updateSuccess);
  const mediaKindValues = Object.keys(MediaKind);

  const handleClose = () => {
    navigate(`/offer-media${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getOffers({}));
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
    values.createdDate = convertDateTimeToServer(values.createdDate);
    values.lastModifiedDate = convertDateTimeToServer(values.lastModifiedDate);

    const entity = {
      ...offerMediaEntity,
      ...values,
      offer: offers.find(it => it.id.toString() === values.offer?.toString()),
      file: fileObjects.find(it => it.id.toString() === values.file?.toString()),
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
          mediaKind: 'IMAGE',
          ...offerMediaEntity,
          createdDate: convertDateTimeFromServer(offerMediaEntity.createdDate),
          lastModifiedDate: convertDateTimeFromServer(offerMediaEntity.lastModifiedDate),
          offer: offerMediaEntity?.offer?.id,
          file: offerMediaEntity?.file?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="freelanceApp.offerMedia.home.createOrEditLabel" data-cy="OfferMediaCreateUpdateHeading">
            Create or edit a Offer Media
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="offer-media-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField label="Media Kind" id="offer-media-mediaKind" name="mediaKind" data-cy="mediaKind" type="select">
                {mediaKindValues.map(mediaKind => (
                  <option value={mediaKind} key={mediaKind}>
                    {mediaKind}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField label="Is Primary" id="offer-media-isPrimary" name="isPrimary" data-cy="isPrimary" check type="checkbox" />
              <ValidatedField
                label="Caption"
                id="offer-media-caption"
                name="caption"
                data-cy="caption"
                type="text"
                validate={{
                  maxLength: { value: 140, message: 'This field cannot be longer than 140 characters.' },
                }}
              />
              <ValidatedField
                label="Created Date"
                id="offer-media-createdDate"
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
                id="offer-media-lastModifiedDate"
                name="lastModifiedDate"
                data-cy="lastModifiedDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label="Created By"
                id="offer-media-createdBy"
                name="createdBy"
                data-cy="createdBy"
                type="text"
                validate={{
                  maxLength: { value: 50, message: 'This field cannot be longer than 50 characters.' },
                }}
              />
              <ValidatedField
                label="Last Modified By"
                id="offer-media-lastModifiedBy"
                name="lastModifiedBy"
                data-cy="lastModifiedBy"
                type="text"
                validate={{
                  maxLength: { value: 50, message: 'This field cannot be longer than 50 characters.' },
                }}
              />
              <ValidatedField id="offer-media-offer" name="offer" data-cy="offer" label="Offer" type="select">
                <option value="" key="0" />
                {offers
                  ? offers.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="offer-media-file" name="file" data-cy="file" label="File" type="select">
                <option value="" key="0" />
                {fileObjects
                  ? fileObjects.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.objectKey}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/offer-media" replace color="info">
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

export default OfferMediaUpdate;
