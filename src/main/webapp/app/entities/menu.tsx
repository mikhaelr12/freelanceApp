import React from 'react';
// eslint-disable-line

import MenuItem from 'app/shared/layout/menus/menu-item'; // eslint-disable-line

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/category">
        Category
      </MenuItem>
      <MenuItem icon="asterisk" to="/subcategory">
        Subcategory
      </MenuItem>
      <MenuItem icon="asterisk" to="/offer-type">
        Offer Type
      </MenuItem>
      <MenuItem icon="asterisk" to="/country">
        Country
      </MenuItem>
      <MenuItem icon="asterisk" to="/skill">
        Skill
      </MenuItem>
      <MenuItem icon="asterisk" to="/offer">
        Offer
      </MenuItem>
      <MenuItem icon="asterisk" to="/offer-package">
        Offer Package
      </MenuItem>
      <MenuItem icon="asterisk" to="/offer-review">
        Offer Review
      </MenuItem>
      <MenuItem icon="asterisk" to="/offer-media">
        Offer Media
      </MenuItem>
      <MenuItem icon="asterisk" to="/profile-review">
        Profile Review
      </MenuItem>
      <MenuItem icon="asterisk" to="/profile">
        Profile
      </MenuItem>
      <MenuItem icon="asterisk" to="/file-object">
        File Object
      </MenuItem>
      <MenuItem icon="asterisk" to="/order">
        Order
      </MenuItem>
      <MenuItem icon="asterisk" to="/tag">
        Tag
      </MenuItem>
      <MenuItem icon="asterisk" to="/favorite-offer">
        Favorite Offer
      </MenuItem>
      <MenuItem icon="asterisk" to="/requirement">
        Requirement
      </MenuItem>
      <MenuItem icon="asterisk" to="/delivery">
        Delivery
      </MenuItem>
      <MenuItem icon="asterisk" to="/dispute">
        Dispute
      </MenuItem>
      <MenuItem icon="asterisk" to="/conversation">
        Conversation
      </MenuItem>
      <MenuItem icon="asterisk" to="/message">
        Message
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
