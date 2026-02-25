import './profile-create.scss';

import React, { FormEvent, useEffect, useMemo, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { Alert, Button, Col, FormGroup, Label, Row } from 'reactstrap';

import { ProfileType } from 'app/shared/model/enumerations/profile-type.model';

interface ISkillOption {
  id?: number;
  name?: string;
  value?: string;
}

interface IProfileCreationForm {
  firstName: string;
  lastName: string;
  description?: string;
  profileType: keyof typeof ProfileType;
}

const ProfileCreate = () => {
  const navigate = useNavigate();
  const [skills, setSkills] = useState<ISkillOption[]>([]);
  const [selectedSkillIds, setSelectedSkillIds] = useState<number[]>([]);
  const [loadingSkills, setLoadingSkills] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [skillsQuery, setSkillsQuery] = useState('');
  const [formValues, setFormValues] = useState<IProfileCreationForm>({
    firstName: '',
    lastName: '',
    description: '',
    profileType: 'FREELANCER',
  });
  const profileTypeValues = Object.keys(ProfileType);
  const isClientProfile = formValues.profileType === 'CLIENT';

  useEffect(() => {
    let stillMounted = true;

    const fetchAllSkills = async () => {
      setLoadingSkills(true);
      try {
        const response = await axios.get<ISkillOption[]>('api/skills/all');
        const loadedSkills = response.data ?? [];
        if (!stillMounted) return;
        const uniqueSkills = loadedSkills.filter((skill, index, arr) => arr.findIndex(item => item.id === skill.id) === index);
        setSkills(uniqueSkills);
      } catch {
        if (!stillMounted) return;
        setSkills([]);
      } finally {
        if (stillMounted) {
          setLoadingSkills(false);
        }
      }
    };

    fetchAllSkills();

    axios
      .get('api/profiles/me')
      .then(() => {
        if (stillMounted) {
          navigate('/', { replace: true });
        }
      })
      .catch(() => {
        // 404 means no profile yet, which is the expected setup flow.
      });

    return () => {
      stillMounted = false;
    };
  }, [navigate]);

  const getSkillLabel = (skill: ISkillOption) => skill.name ?? skill.value ?? `Skill ${skill.id}`;

  const filteredSkills = useMemo(() => {
    const normalizedQuery = skillsQuery.trim().toLowerCase();
    if (!normalizedQuery) return skills;
    return skills.filter(skill => getSkillLabel(skill).toLowerCase().includes(normalizedQuery));
  }, [skills, skillsQuery]);

  const selectedSkills = useMemo(
    () =>
      skills
        .filter(skill => selectedSkillIds.includes(skill.id ?? -1))
        .filter((skill): skill is ISkillOption & { id: number } => typeof skill.id === 'number')
        .map(skill => ({ id: skill.id, label: getSkillLabel(skill) })),
    [skills, selectedSkillIds],
  );

  const toggleSkill = (skillId?: number) => {
    if (!skillId) return;
    setSelectedSkillIds(previous => (previous.includes(skillId) ? previous.filter(id => id !== skillId) : [...previous, skillId]));
  };

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const normalizedFirstName = (formValues.firstName ?? '').trim();
    const normalizedLastName = (formValues.lastName ?? '').trim();

    if (!normalizedFirstName || !normalizedLastName) {
      setErrorMessage('First name and last name are required.');
      return;
    }

    setIsSubmitting(true);
    setErrorMessage(null);

    const payload = {
      firstName: normalizedFirstName,
      lastName: normalizedLastName,
      description: formValues.description?.trim() || null,
      profileType: formValues.profileType,
      skillIds: isClientProfile ? [] : selectedSkillIds,
    };

    axios
      .post('api/profiles', payload)
      .then(() => {
        navigate('/', { replace: true });
      })
      .catch(error => {
        if (error?.response?.status === 400) {
          const validationErrors = error?.response?.data?.fieldErrors ?? [];
          const hasNameValidationError = validationErrors.some(fieldError =>
            ['firstName', 'lastName'].includes(fieldError?.field as string),
          );
          if (hasNameValidationError) {
            setErrorMessage('First name and last name are required.');
            return;
          }
          setErrorMessage('A profile already exists for this account.');
          return;
        }
        setErrorMessage('Profile could not be created. Please try again.');
      })
      .finally(() => {
        setIsSubmitting(false);
      });
  };

  return (
    <div className="profile-create-page">
      <Row className="justify-content-center">
        <Col md="10" lg="8">
          <div className="profile-create-card">
            <div className="profile-create-header">
              <h2>Create your profile</h2>
              <p>Tell clients what you do best. You can edit this later from settings.</p>
            </div>

            {errorMessage ? <Alert color="danger">{errorMessage}</Alert> : null}

            <form onSubmit={handleSubmit}>
              <div className="profile-create-grid">
                <FormGroup>
                  <Label for="profile-create-first-name">First name</Label>
                  <input
                    id="profile-create-first-name"
                    type="text"
                    className="form-control"
                    value={formValues.firstName}
                    onChange={event => setFormValues(previous => ({ ...previous, firstName: event.target.value }))}
                    maxLength={20}
                    required
                  />
                </FormGroup>
                <FormGroup>
                  <Label for="profile-create-last-name">Last name</Label>
                  <input
                    id="profile-create-last-name"
                    type="text"
                    className="form-control"
                    value={formValues.lastName}
                    onChange={event => setFormValues(previous => ({ ...previous, lastName: event.target.value }))}
                    maxLength={20}
                    required
                  />
                </FormGroup>
              </div>
              <FormGroup>
                <Label for="profile-create-type">Profile type</Label>
                <select
                  id="profile-create-type"
                  className="form-select"
                  value={formValues.profileType}
                  onChange={event =>
                    setFormValues(previous => ({ ...previous, profileType: event.target.value as keyof typeof ProfileType }))
                  }
                >
                  {profileTypeValues.map(profileType => (
                    <option value={profileType} key={profileType}>
                      {profileType}
                    </option>
                  ))}
                </select>
              </FormGroup>
              <FormGroup>
                <Label for="profile-create-description">Description</Label>
                <textarea
                  id="profile-create-description"
                  className="form-control"
                  value={formValues.description}
                  onChange={event => setFormValues(previous => ({ ...previous, description: event.target.value }))}
                  placeholder="What services do you provide? What kind of projects do you prefer?"
                  rows={4}
                />
              </FormGroup>
              {!isClientProfile ? (
                <FormGroup className="skills-field">
                  <div className="skills-headline">
                    <Label for="profile-create-skills-search">Skills</Label>
                    <span>{selectedSkillIds.length} selected</span>
                  </div>
                  <input
                    id="profile-create-skills-search"
                    className="form-control"
                    type="text"
                    value={skillsQuery}
                    onChange={event => setSkillsQuery(event.target.value)}
                    placeholder="Search skills..."
                    disabled={loadingSkills}
                  />
                  <div className="skills-panel" id="profile-create-skills">
                    {loadingSkills ? <div className="skills-state">Loading skills...</div> : null}
                    {!loadingSkills && filteredSkills.length === 0 ? <div className="skills-state">No skills found.</div> : null}
                    {!loadingSkills &&
                      filteredSkills.map(skill => {
                        const skillId = skill.id;
                        if (!skillId) return null;
                        const selected = selectedSkillIds.includes(skillId);
                        return (
                          <button
                            type="button"
                            key={skillId}
                            className={`skill-chip ${selected ? 'selected' : ''}`}
                            onClick={() => toggleSkill(skillId)}
                          >
                            {getSkillLabel(skill)}
                          </button>
                        );
                      })}
                  </div>
                  {selectedSkills.length > 0 ? (
                    <div className="selected-skills">
                      {selectedSkills.slice(0, 8).map(skill => (
                        <button
                          type="button"
                          key={skill.id}
                          className="selected-pill"
                          onClick={() => toggleSkill(skill.id)}
                          aria-label={`Remove ${skill.label}`}
                          title={`Remove ${skill.label}`}
                        >
                          {skill.label} <span aria-hidden="true">×</span>
                        </button>
                      ))}
                      {selectedSkills.length > 8 ? (
                        <span className="selected-pill more-pill">+{selectedSkills.length - 8} more</span>
                      ) : null}
                    </div>
                  ) : null}
                </FormGroup>
              ) : null}
              <Button color="primary" className="create-profile-btn" type="submit" disabled={isSubmitting}>
                {isSubmitting ? 'Creating...' : 'Create profile'}
              </Button>
            </form>
          </div>
        </Col>
      </Row>
    </div>
  );
};

export default ProfileCreate;
