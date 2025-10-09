import category from 'app/entities/category/category.reducer';
import subcategory from 'app/entities/subcategory/subcategory.reducer';
import offerType from 'app/entities/offer-type/offer-type.reducer';
import country from 'app/entities/country/country.reducer';
import skill from 'app/entities/skill/skill.reducer';
import offer from 'app/entities/offer/offer.reducer';
import offerPackage from 'app/entities/offer-package/offer-package.reducer';
import offerReview from 'app/entities/offer-review/offer-review.reducer';
import offerMedia from 'app/entities/offer-media/offer-media.reducer';
import profileReview from 'app/entities/profile-review/profile-review.reducer';
import profile from 'app/entities/profile/profile.reducer';
import fileObject from 'app/entities/file-object/file-object.reducer';
import order from 'app/entities/order/order.reducer';
import tag from 'app/entities/tag/tag.reducer';
import favoriteOffer from 'app/entities/favorite-offer/favorite-offer.reducer';
import requirement from 'app/entities/requirement/requirement.reducer';
import delivery from 'app/entities/delivery/delivery.reducer';
import dispute from 'app/entities/dispute/dispute.reducer';
import conversation from 'app/entities/conversation/conversation.reducer';
import message from 'app/entities/message/message.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  category,
  subcategory,
  offerType,
  country,
  skill,
  offer,
  offerPackage,
  offerReview,
  offerMedia,
  profileReview,
  profile,
  fileObject,
  order,
  tag,
  favoriteOffer,
  requirement,
  delivery,
  dispute,
  conversation,
  message,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
