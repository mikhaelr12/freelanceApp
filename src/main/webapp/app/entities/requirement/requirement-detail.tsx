import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './requirement.reducer';

export const RequirementDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const requirementEntity = useAppSelector(state => state.requirement.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="requirementDetailsHeading">Requirement</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{requirementEntity.id}</dd>
          <dt>
            <span id="prompt">Prompt</span>
          </dt>
          <dd>{requirementEntity.prompt}</dd>
          <dt>
            <span id="answer">Answer</span>
          </dt>
          <dd>{requirementEntity.answer}</dd>
          <dt>Order</dt>
          <dd>{requirementEntity.order ? requirementEntity.order.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/requirement" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/requirement/${requirementEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default RequirementDetail;
