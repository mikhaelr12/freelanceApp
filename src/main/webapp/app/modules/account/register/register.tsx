import React, { useEffect, useState } from 'react';
import { isEmail, ValidatedField, ValidatedForm } from 'react-jhipster';
import { Alert, Button, Col, Modal, ModalBody, ModalFooter, ModalHeader, Row } from 'reactstrap';
import { toast } from 'react-toastify';
import { Link, useNavigate } from 'react-router-dom';

import 'app/modules/login/auth-modal.scss';
import PasswordStrengthBar from 'app/shared/layout/password/password-strength-bar';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { handleRegister, reset } from './register.reducer';

export const RegisterPage = () => {
  const [password, setPassword] = useState('');
  const [showModal, setShowModal] = useState(true);
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  useEffect(
    () => () => {
      dispatch(reset());
    },
    [],
  );

  const handleValidSubmit = ({ username, email, firstPassword }) => {
    dispatch(handleRegister({ login: username, email, password: firstPassword, langKey: 'en' }));
  };

  const updatePassword = event => setPassword(event.target.value);

  const successMessage = useAppSelector(state => state.register.successMessage);

  useEffect(() => {
    if (successMessage) {
      toast.success(successMessage);
    }
  }, [successMessage]);

  const handleClose = () => {
    setShowModal(false);
    navigate('/');
  };

  return (
    <Modal isOpen={showModal} toggle={handleClose} backdrop="static" id="register-page" autoFocus={false} className="auth-modal">
      <ModalHeader id="register-title" data-cy="registerTitle" toggle={handleClose}>
        Sign up
      </ModalHeader>
      <ModalBody>
        <Row className="justify-content-center">
          <Col md="12">
            <ValidatedForm id="register-form" onSubmit={handleValidSubmit}>
              <ValidatedField
                name="username"
                label="Username"
                placeholder="Your username"
                validate={{
                  required: { value: true, message: 'Your username is required.' },
                  pattern: {
                    value: /^[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$|^[_.@A-Za-z0-9-]+$/,
                    message: 'Your username is invalid.',
                  },
                  minLength: { value: 1, message: 'Your username is required to be at least 1 character.' },
                  maxLength: { value: 50, message: 'Your username cannot be longer than 50 characters.' },
                }}
                data-cy="username"
              />
              <ValidatedField
                name="email"
                label="Email"
                placeholder="Your email"
                type="email"
                validate={{
                  required: { value: true, message: 'Your email is required.' },
                  minLength: { value: 5, message: 'Your email is required to be at least 5 characters.' },
                  maxLength: { value: 254, message: 'Your email cannot be longer than 50 characters.' },
                  validate: v => isEmail(v) || 'Your email is invalid.',
                }}
                data-cy="email"
              />
              <ValidatedField
                name="firstPassword"
                label="New password"
                placeholder="New password"
                type="password"
                onChange={updatePassword}
                validate={{
                  required: { value: true, message: 'Your password is required.' },
                  minLength: { value: 4, message: 'Your password is required to be at least 4 characters.' },
                  maxLength: { value: 50, message: 'Your password cannot be longer than 50 characters.' },
                }}
                data-cy="firstPassword"
              />
              <PasswordStrengthBar password={password} />
              <ValidatedField
                name="secondPassword"
                label="New password confirmation"
                placeholder="Confirm the new password"
                type="password"
                validate={{
                  required: { value: true, message: 'Your confirmation password is required.' },
                  minLength: { value: 4, message: 'Your confirmation password is required to be at least 4 characters.' },
                  maxLength: { value: 50, message: 'Your confirmation password cannot be longer than 50 characters.' },
                  validate: v => v === password || 'The password and its confirmation do not match!',
                }}
                data-cy="secondPassword"
              />
              <Alert color="warning" className="mt-3 mb-0">
                Already have an account?{' '}
                <Link to="/login" className="alert-link">
                  Sign in
                </Link>
              </Alert>
              <ModalFooter className="px-0 pb-0">
                <Button color="secondary" onClick={handleClose} tabIndex={1}>
                  Cancel
                </Button>
                <Button id="register-submit" color="primary" type="submit" data-cy="submit">
                  Register
                </Button>
              </ModalFooter>
            </ValidatedForm>
          </Col>
        </Row>
      </ModalBody>
    </Modal>
  );
};

export default RegisterPage;
