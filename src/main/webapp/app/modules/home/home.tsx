import './home.scss';

import React, { useEffect, useMemo, useState } from 'react';
import axios from 'axios';
import { Link as RouterLink } from 'react-router-dom';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCategoryEntities } from 'app/entities/category/category.reducer';
import { getEntities as getSubcategoryEntities } from 'app/entities/subcategory/subcategory.reducer';

const CATEGORY_FETCH_SIZE = 2000;
const SUBCATEGORY_FETCH_SIZE = 5000;
const FREELANCER_FETCH_SIZE = 200;
const FREELANCER_DISPLAY_COUNT = 8;

interface ISkillShortDTO {
  id?: number;
  name?: string;
  value?: string;
}

interface IProfileDTO {
  id?: number;
  firstName?: string;
  lastName?: string;
  description?: string;
  profileType?: string;
  rating?: number;
  reviewCount?: number;
  skills?: ISkillShortDTO[];
  imageBase64?: string;
  skillShortDTOs?: ISkillShortDTO[];
  skillShortDtos?: ISkillShortDTO[];
}

export const Home = () => {
  const dispatch = useAppDispatch();
  const categoryList = useAppSelector(state => state.category.entities);
  const subcategoryList = useAppSelector(state => state.subcategory.entities);
  const categoriesLoading = useAppSelector(state => state.category.loading);
  const [searchQuery, setSearchQuery] = useState('');
  const [activeSlide, setActiveSlide] = useState(0);
  const [isCarouselPaused, setIsCarouselPaused] = useState(false);
  const [freelancerPool, setFreelancerPool] = useState<IProfileDTO[]>([]);
  const [featuredFreelancers, setFeaturedFreelancers] = useState<IProfileDTO[]>([]);
  const [freelancersLoading, setFreelancersLoading] = useState(false);

  useEffect(() => {
    dispatch(
      getCategoryEntities({
        page: 0,
        size: CATEGORY_FETCH_SIZE,
        sort: 'name,asc',
      }),
    );
    dispatch(
      getSubcategoryEntities({
        page: 0,
        size: SUBCATEGORY_FETCH_SIZE,
        sort: 'name,asc',
      }),
    );
  }, [dispatch]);

  const pickRandomFreelancers = (freelancers: IProfileDTO[]) => {
    const shuffled = [...freelancers];
    for (let i = shuffled.length - 1; i > 0; i -= 1) {
      const j = Math.floor(Math.random() * (i + 1));
      const temp = shuffled[i];
      shuffled[i] = shuffled[j];
      shuffled[j] = temp;
    }
    return shuffled.slice(0, FREELANCER_DISPLAY_COUNT);
  };

  const loadFreelancerDetails = async (profiles: IProfileDTO[]) => {
    const profilesWithId = profiles.filter(profile => profile.id);
    const responses = await Promise.all(profilesWithId.map(profile => axios.get<IProfileDTO>(`api/profiles/${profile.id}`)));
    return responses.map(response => response.data);
  };

  const refreshFreelancerSelection = async (poolParam?: IProfileDTO[]) => {
    const sourcePool = poolParam ?? freelancerPool;
    if (sourcePool.length === 0) {
      setFeaturedFreelancers([]);
      return;
    }
    setFreelancersLoading(true);
    try {
      const randomProfiles = pickRandomFreelancers(sourcePool);
      const detailedProfiles = await loadFreelancerDetails(randomProfiles);
      setFeaturedFreelancers(detailedProfiles);
    } catch {
      setFeaturedFreelancers([]);
    } finally {
      setFreelancersLoading(false);
    }
  };

  useEffect(() => {
    const fetchFreelancers = async () => {
      setFreelancersLoading(true);
      try {
        const response = await axios.get<IProfileDTO[]>(
          `api/profiles?page=0&size=${FREELANCER_FETCH_SIZE}&sort=id,desc&profileType.equals=FREELANCER`,
        );
        const profiles = response.data.filter(profile => profile.id);
        setFreelancerPool(profiles);
        await refreshFreelancerSelection(profiles);
      } catch {
        setFreelancerPool([]);
        setFeaturedFreelancers([]);
      }
    };

    fetchFreelancers();
  }, []);

  const activeCategories = useMemo(() => categoryList.filter(category => category.active !== false), [categoryList]);
  const featuredCategories = useMemo(() => activeCategories.slice(0, 6), [activeCategories]);

  const popularTags = useMemo(
    () =>
      subcategoryList
        .filter(subcategory => subcategory.active !== false && subcategory.name)
        .slice(0, 4)
        .map(subcategory => subcategory.name as string),
    [subcategoryList],
  );

  const normalizedSubcategories = useMemo(
    () =>
      subcategoryList
        .map(subcategory => {
          const dynamicSubcategory = subcategory as unknown as {
            id?: number;
            name?: string;
            category?: { id?: number } | null;
            subcategoryId?: number;
            categoryShortDTO?: { categoryId?: number; name?: string } | null;
          };

          return {
            id: dynamicSubcategory.id ?? dynamicSubcategory.subcategoryId,
            name: dynamicSubcategory.name,
            categoryId: dynamicSubcategory.category?.id ?? dynamicSubcategory.categoryShortDTO?.categoryId,
          };
        })
        .filter(subcategory => Boolean(subcategory.name)),
    [subcategoryList],
  );

  const getCategoryIcon = (categoryName?: string) => {
    const normalizedName = categoryName?.toLowerCase() ?? '';
    if (normalizedName.includes('web')) return 'WEB';
    if (normalizedName.includes('mobile')) return 'APP';
    if (normalizedName.includes('design')) return 'DES';
    if (normalizedName.includes('data') || normalizedName.includes('ai')) return 'AI';
    if (normalizedName.includes('cloud') || normalizedName.includes('devops')) return 'OPS';
    if (normalizedName.includes('market')) return 'MKT';
    return 'GEN';
  };

  const getCategoryDescription = (categoryName?: string) => {
    const normalizedName = categoryName?.toLowerCase() ?? '';
    if (normalizedName.includes('web')) return 'Build modern websites and web applications.';
    if (normalizedName.includes('mobile')) return 'Native and cross-platform mobile app services.';
    if (normalizedName.includes('design')) return 'UI/UX design, branding, and creative work.';
    if (normalizedName.includes('data') || normalizedName.includes('ai')) return 'Data analysis, machine learning, and AI solutions.';
    if (normalizedName.includes('cloud') || normalizedName.includes('devops')) return 'Cloud infrastructure and deployment automation.';
    if (normalizedName.includes('market')) return 'SEO, social media, and growth marketing services.';
    return 'Explore specialist services across this domain.';
  };

  useEffect(() => {
    if (featuredCategories.length === 0) {
      setActiveSlide(0);
      return;
    }

    if (activeSlide > featuredCategories.length - 1) {
      setActiveSlide(0);
    }
  }, [activeSlide, featuredCategories.length]);

  const goToPreviousSlide = () => {
    if (featuredCategories.length === 0) return;
    setActiveSlide(previous => (previous === 0 ? featuredCategories.length - 1 : previous - 1));
  };

  const goToNextSlide = () => {
    if (featuredCategories.length === 0) return;
    setActiveSlide(previous => (previous === featuredCategories.length - 1 ? 0 : previous + 1));
  };

  const normalizeBase64 = (value: string) => value.replace(/\s/g, '').replace(/-/g, '+').replace(/_/g, '/');

  const detectImageMimeType = (base64Value: string) => {
    if (base64Value.startsWith('/9j/')) return 'image/jpeg';
    if (base64Value.startsWith('iVBORw0KGgo')) return 'image/png';
    if (base64Value.startsWith('R0lGOD')) return 'image/gif';
    if (base64Value.startsWith('UklGR')) return 'image/webp';
    return 'image/jpeg';
  };

  const getProfileImageSrc = (imageBase64?: string) => {
    if (!imageBase64) return null;
    if (imageBase64.startsWith('data:')) return imageBase64;
    const normalizedBase64 = normalizeBase64(imageBase64);
    if (!normalizedBase64) return null;
    const mimeType = detectImageMimeType(normalizedBase64);
    return `data:${mimeType};base64,${normalizedBase64}`;
  };

  const getFreelancerInitials = (profile: IProfileDTO) => {
    const first = profile.firstName?.trim().charAt(0) ?? '';
    const last = profile.lastName?.trim().charAt(0) ?? '';
    const initials = `${first}${last}`.toUpperCase();
    return initials || 'FR';
  };

  const formatProfileType = (profileType?: string) => {
    if (!profileType) return 'Freelancer';
    return profileType
      .toLowerCase()
      .split('_')
      .map(word => word.charAt(0).toUpperCase() + word.slice(1))
      .join(' ');
  };

  const formatRating = (rating?: number) => {
    if (typeof rating !== 'number' || Number.isNaN(rating)) return 'New';
    return rating.toFixed(1);
  };

  const formatReviewCount = (reviewCount?: number) => {
    if (!reviewCount || reviewCount < 1) return '0';
    return `${reviewCount}`;
  };

  const extractSkillNames = (profile: IProfileDTO) => {
    const dynamicProfile = profile as unknown as {
      skills?: unknown;
      skillShortDTOs?: unknown;
      skillShortDtos?: unknown;
    };

    const rawSkills = dynamicProfile.skills ?? dynamicProfile.skillShortDTOs ?? dynamicProfile.skillShortDtos ?? [];

    let skillItems: unknown[] = [];
    if (Array.isArray(rawSkills)) {
      skillItems = rawSkills;
    } else if (typeof rawSkills === 'string') {
      try {
        const parsed = JSON.parse(rawSkills);
        skillItems = Array.isArray(parsed) ? parsed : [];
      } catch {
        skillItems = [];
      }
    } else if (rawSkills && typeof rawSkills === 'object') {
      skillItems = Object.values(rawSkills);
    }

    return skillItems
      .map(skill => {
        if (typeof skill === 'string') return skill;
        if (!skill || typeof skill !== 'object') return '';

        const dynamicSkill = skill as {
          name?: string;
          value?: string;
          skill?: { name?: string; value?: string };
        };

        return dynamicSkill.name ?? dynamicSkill.value ?? dynamicSkill.skill?.name ?? dynamicSkill.skill?.value ?? '';
      })
      .map(skill => skill.trim())
      .filter(Boolean);
  };

  useEffect(() => {
    if (featuredCategories.length < 2 || isCarouselPaused) return;

    const sliderInterval = window.setInterval(() => {
      setActiveSlide(previous => (previous === featuredCategories.length - 1 ? 0 : previous + 1));
    }, 3200);

    return () => {
      window.clearInterval(sliderInterval);
    };
  }, [featuredCategories.length, isCarouselPaused]);

  return (
    <div className="home-page">
      <section className="home-hero">
        <div className="hero-content">
          <h1>Find Expert Freelancers For Any Project</h1>
          <p>Connect with top talent across development, design, marketing, and more. Get your project done right.</p>
          <form className="hero-search" onSubmit={event => event.preventDefault()}>
            <input
              type="text"
              value={searchQuery}
              placeholder="What service are you looking for?"
              onChange={event => setSearchQuery(event.target.value)}
            />
            <button type="submit">Search</button>
          </form>
          <div className="popular-tags">
            <span>Popular:</span>
            {popularTags.length > 0 ? (
              popularTags.map(tag => (
                <span className="tag-pill" key={tag}>
                  {tag}
                </span>
              ))
            ) : (
              <>
                <span className="tag-pill">React</span>
                <span className="tag-pill">Logo Design</span>
                <span className="tag-pill">SEO</span>
              </>
            )}
          </div>
        </div>
      </section>

      <section className="home-categories">
        <h2>Browse Categories</h2>
        <p>Explore services across all fields</p>

        {categoriesLoading ? <div className="section-state">Loading categories...</div> : null}
        {!categoriesLoading && featuredCategories.length === 0 ? (
          <div className="section-state">No categories available right now.</div>
        ) : null}

        {!categoriesLoading && featuredCategories.length > 0 ? (
          <div
            className="categories-carousel"
            onMouseEnter={() => setIsCarouselPaused(true)}
            onMouseLeave={() => setIsCarouselPaused(false)}
          >
            <button type="button" className="carousel-control prev" onClick={goToPreviousSlide} aria-label="Previous category">
              ‹
            </button>
            <div className="carousel-track">
              {featuredCategories.map((category, index) => {
                const categorySubcategories = normalizedSubcategories
                  .filter(subcategory => subcategory.categoryId === category.id)
                  .map(subcategory => subcategory.name)
                  .filter(Boolean) as string[];

                const visibleSubcategories = categorySubcategories.slice(0, 3);
                const remainingSubcategories = categorySubcategories.length - visibleSubcategories.length;

                return (
                  <article className={`category-card ${index === activeSlide ? 'active' : ''}`} key={category.id}>
                    <div className="category-icon">{getCategoryIcon(category.name)}</div>
                    <h3>{category.name}</h3>
                    <p>{getCategoryDescription(category.name)}</p>
                    <div className="subcategory-pills">
                      {visibleSubcategories.map(subcategoryName => (
                        <span className="subcategory-pill" key={subcategoryName}>
                          {subcategoryName}
                        </span>
                      ))}
                    </div>
                    {remainingSubcategories > 0 ? <span className="subcategories-more">+{remainingSubcategories} more</span> : null}
                  </article>
                );
              })}
            </div>
            <button type="button" className="carousel-control next" onClick={goToNextSlide} aria-label="Next category">
              ›
            </button>
            <div className="carousel-dots">
              {featuredCategories.map((category, index) => (
                <button
                  key={category.id}
                  type="button"
                  className={`carousel-dot ${index === activeSlide ? 'active' : ''}`}
                  onClick={() => setActiveSlide(index)}
                  aria-label={`Go to ${category.name} category`}
                />
              ))}
            </div>
          </div>
        ) : null}
      </section>

      <section className="home-freelancers">
        <div className="freelancers-header">
          <div>
            <h2>Browse Freelancers</h2>
            <p>Fresh random picks from active profiles on the platform</p>
          </div>
          <button
            type="button"
            className="shuffle-btn"
            onClick={() => void refreshFreelancerSelection()}
            disabled={freelancerPool.length === 0 || freelancersLoading}
          >
            Shuffle picks
          </button>
        </div>

        {freelancersLoading ? <div className="section-state">Loading freelancers...</div> : null}
        {!freelancersLoading && featuredFreelancers.length === 0 ? (
          <div className="section-state">No freelancer profiles available right now.</div>
        ) : null}

        {!freelancersLoading && featuredFreelancers.length > 0 ? (
          <div className="freelancers-grid">
            {featuredFreelancers.map(profile => {
              const fullName = `${profile.firstName ?? ''} ${profile.lastName ?? ''}`.trim() || `Freelancer #${profile.id}`;
              const profileImage = getProfileImageSrc(profile.imageBase64);
              const allSkills = extractSkillNames(profile);
              const visibleSkills = allSkills.slice(0, 2);
              const hiddenSkillsCount = allSkills.length - visibleSkills.length;
              const profileUrl = `/profile/${profile.id}`;

              return (
                <article className="freelancer-card" key={profile.id}>
                  <div className="freelancer-top">
                    <RouterLink className="freelancer-avatar-link" to={profileUrl}>
                      {profileImage ? (
                        <img src={profileImage} alt={fullName} className="freelancer-avatar" />
                      ) : (
                        <div className="freelancer-avatar fallback">{getFreelancerInitials(profile)}</div>
                      )}
                    </RouterLink>
                    <div className="freelancer-meta">
                      <h3>
                        <RouterLink to={profileUrl}>{fullName}</RouterLink>
                      </h3>
                      <span>{formatProfileType(profile.profileType)}</span>
                    </div>
                  </div>
                  <div className="freelancer-rating">
                    <span className="star" aria-hidden="true">
                      ★
                    </span>
                    <span>{formatRating(profile.rating)}</span>
                    <span className="reviews">({formatReviewCount(profile.reviewCount)} reviews)</span>
                  </div>
                  <p>{profile.description?.trim() || 'Experienced freelancer ready to help with your next project.'}</p>
                  <div className="freelancer-skills">
                    {visibleSkills.length > 0 ? (
                      visibleSkills.map(skill => (
                        <span key={skill} className="freelancer-skill">
                          {skill}
                        </span>
                      ))
                    ) : (
                      <span className="freelancer-skill">Available now</span>
                    )}
                    {hiddenSkillsCount > 0 ? <span className="freelancer-skill more-skill">+{hiddenSkillsCount}</span> : null}
                  </div>
                  <RouterLink className="see-profile-btn" to={profileUrl}>
                    See profile
                  </RouterLink>
                </article>
              );
            })}
          </div>
        ) : null}
      </section>

      <footer className="home-footer">
        <div className="footer-inner">
          <div className="footer-brand">
            <strong>FreelanceHub</strong>
          </div>
          <p>Find trusted freelancers and manage projects in one place.</p>
          <span className="footer-meta">© 2026 FreelanceHub. All rights reserved.</span>
        </div>
      </footer>
    </div>
  );
};

export default Home;
