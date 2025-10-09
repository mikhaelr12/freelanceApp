import React from 'react';
import { Route } from 'react-router'; // eslint-disable-line

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Category from './category';
import Subcategory from './subcategory';
import OfferType from './offer-type';
import Country from './country';
import Skill from './skill';
import Offer from './offer';
import OfferPackage from './offer-package';
import OfferReview from './offer-review';
import OfferMedia from './offer-media';
import ProfileReview from './profile-review';
import Profile from './profile';
import FileObject from './file-object';
import Order from './order';
import Tag from './tag';
import FavoriteOffer from './favorite-offer';
import Requirement from './requirement';
import Delivery from './delivery';
import Dispute from './dispute';
import Conversation from './conversation';
import Message from './message';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="category/*" element={<Category />} />
        <Route path="subcategory/*" element={<Subcategory />} />
        <Route path="offer-type/*" element={<OfferType />} />
        <Route path="country/*" element={<Country />} />
        <Route path="skill/*" element={<Skill />} />
        <Route path="offer/*" element={<Offer />} />
        <Route path="offer-package/*" element={<OfferPackage />} />
        <Route path="offer-review/*" element={<OfferReview />} />
        <Route path="offer-media/*" element={<OfferMedia />} />
        <Route path="profile-review/*" element={<ProfileReview />} />
        <Route path="profile/*" element={<Profile />} />
        <Route path="file-object/*" element={<FileObject />} />
        <Route path="order/*" element={<Order />} />
        <Route path="tag/*" element={<Tag />} />
        <Route path="favorite-offer/*" element={<FavoriteOffer />} />
        <Route path="requirement/*" element={<Requirement />} />
        <Route path="delivery/*" element={<Delivery />} />
        <Route path="dispute/*" element={<Dispute />} />
        <Route path="conversation/*" element={<Conversation />} />
        <Route path="message/*" element={<Message />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
