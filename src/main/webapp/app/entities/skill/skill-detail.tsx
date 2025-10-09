import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './skill.reducer';

export const SkillDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const skillEntity = useAppSelector(state => state.skill.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="skillDetailsHeading">Skill</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{skillEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{skillEntity.name}</dd>
          <dt>
            <span id="createdDate">Created Date</span>
          </dt>
          <dd>{skillEntity.createdDate ? <TextFormat value={skillEntity.createdDate} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="lastModifiedDate">Last Modified Date</span>
          </dt>
          <dd>
            {skillEntity.lastModifiedDate ? <TextFormat value={skillEntity.lastModifiedDate} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="createdBy">Created By</span>
          </dt>
          <dd>{skillEntity.createdBy}</dd>
          <dt>
            <span id="lastModifiedBy">Last Modified By</span>
          </dt>
          <dd>{skillEntity.lastModifiedBy}</dd>
          <dt>
            <span id="active">Active</span>
          </dt>
          <dd>{skillEntity.active ? 'true' : 'false'}</dd>
          <dt>Category</dt>
          <dd>{skillEntity.category ? skillEntity.category.name : ''}</dd>
          <dt>Profile</dt>
          <dd>
            {skillEntity.profiles
              ? skillEntity.profiles.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {skillEntity.profiles && i === skillEntity.profiles.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/skill" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/skill/${skillEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default SkillDetail;
