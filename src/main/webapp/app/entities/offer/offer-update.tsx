import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getProfiles } from 'app/entities/profile/profile.reducer';
import { getEntities as getOfferTypes } from 'app/entities/offer-type/offer-type.reducer';
import { getEntities as getTags } from 'app/entities/tag/tag.reducer';
import { OfferStatus } from 'app/shared/model/enumerations/offer-status.model';
import { createEntity, getEntity, reset, updateEntity } from './offer.reducer';

export const OfferUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const profiles = useAppSelector(state => state.profile.entities);
  const offerTypes = useAppSelector(state => state.offerType.entities);
  const tags = useAppSelector(state => state.tag.entities);
  const offerEntity = useAppSelector(state => state.offer.entity);
  const loading = useAppSelector(state => state.offer.loading);
  const updating = useAppSelector(state => state.offer.updating);
  const updateSuccess = useAppSelector(state => state.offer.updateSuccess);
  const offerStatusValues = Object.keys(OfferStatus);

  const handleClose = () => {
    navigate(`/offer${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getProfiles({}));
    dispatch(getOfferTypes({}));
    dispatch(getTags({}));
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
    if (values.rating !== undefined && typeof values.rating !== 'number') {
      values.rating = Number(values.rating);
    }
    values.createdDate = convertDateTimeToServer(values.createdDate);
    values.lastModifiedDate = convertDateTimeToServer(values.lastModifiedDate);

    const entity = {
      ...offerEntity,
      ...values,
      owner: profiles.find(it => it.id.toString() === values.owner?.toString()),
      offertype: offerTypes.find(it => it.id.toString() === values.offertype?.toString()),
      tags: mapIdList(values.tags),
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
          status: 'ACTIVE',
          ...offerEntity,
          createdDate: convertDateTimeFromServer(offerEntity.createdDate),
          lastModifiedDate: convertDateTimeFromServer(offerEntity.lastModifiedDate),
          owner: offerEntity?.owner?.id,
          offertype: offerEntity?.offertype?.id,
          tags: offerEntity?.tags?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="freelanceApp.offer.home.createOrEditLabel" data-cy="OfferCreateUpdateHeading">
            Create or edit a Offer
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="offer-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Name"
                id="offer-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  maxLength: { value: 255, message: 'This field cannot be longer than 255 characters.' },
                }}
              />
              <ValidatedField
                label="Description"
                id="offer-description"
                name="description"
                data-cy="description"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  maxLength: { value: 2048, message: 'This field cannot be longer than 2048 characters.' },
                }}
              />
              <ValidatedField
                label="Rating"
                id="offer-rating"
                name="rating"
                data-cy="rating"
                type="text"
                validate={{
                  min: { value: 0, message: 'This field should be at least 0.' },
                  max: { value: 5, message: 'This field cannot be more than 5.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <ValidatedField label="Status" id="offer-status" name="status" data-cy="status" type="select">
                {offerStatusValues.map(offerStatus => (
                  <option value={offerStatus} key={offerStatus}>
                    {offerStatus}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField label="Visibility" id="offer-visibility" name="visibility" data-cy="visibility" check type="checkbox" />
              <ValidatedField
                label="Created Date"
                id="offer-createdDate"
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
                id="offer-lastModifiedDate"
                name="lastModifiedDate"
                data-cy="lastModifiedDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label="Created By"
                id="offer-createdBy"
                name="createdBy"
                data-cy="createdBy"
                type="text"
                validate={{
                  maxLength: { value: 50, message: 'This field cannot be longer than 50 characters.' },
                }}
              />
              <ValidatedField
                label="Last Modified By"
                id="offer-lastModifiedBy"
                name="lastModifiedBy"
                data-cy="lastModifiedBy"
                type="text"
                validate={{
                  maxLength: { value: 50, message: 'This field cannot be longer than 50 characters.' },
                }}
              />
              <ValidatedField id="offer-owner" name="owner" data-cy="owner" label="Owner" type="select">
                <option value="" key="0" />
                {profiles
                  ? profiles.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="offer-offertype" name="offertype" data-cy="offertype" label="Offertype" type="select">
                <option value="" key="0" />
                {offerTypes
                  ? offerTypes.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField label="Tag" id="offer-tag" data-cy="tag" type="select" multiple name="tags">
                <option value="" key="0" />
                {tags
                  ? tags.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/offer" replace color="info">
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

export default OfferUpdate;
