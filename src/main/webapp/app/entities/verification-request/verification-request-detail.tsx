import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './verification-request.reducer';

export const VerificationRequestDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const verificationRequestEntity = useAppSelector(state => state.verificationRequest.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="verificationRequestDetailsHeading">Verification Request</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{verificationRequestEntity.id}</dd>
          <dt>Profile</dt>
          <dd>{verificationRequestEntity.profile ? verificationRequestEntity.profile.id : ''}</dd>
          <dt>File Object</dt>
          <dd>{verificationRequestEntity.fileObject ? verificationRequestEntity.fileObject.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/verification-request" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/verification-request/${verificationRequestEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default VerificationRequestDetail;
