import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './message.reducer';

export const MessageDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const messageEntity = useAppSelector(state => state.message.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="messageDetailsHeading">Message</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{messageEntity.id}</dd>
          <dt>
            <span id="body">Body</span>
          </dt>
          <dd>{messageEntity.body}</dd>
          <dt>
            <span id="sentAt">Sent At</span>
          </dt>
          <dd>{messageEntity.sentAt ? <TextFormat value={messageEntity.sentAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>Conversation</dt>
          <dd>{messageEntity.conversation ? messageEntity.conversation.id : ''}</dd>
          <dt>Sender</dt>
          <dd>{messageEntity.sender ? messageEntity.sender.login : ''}</dd>
        </dl>
        <Button tag={Link} to="/message" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/message/${messageEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default MessageDetail;
