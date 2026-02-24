import './header.scss';

import React, { useEffect, useMemo, useRef, useState } from 'react';
import { Link as RouterLink } from 'react-router-dom';

import { Collapse, Nav, Navbar, NavbarToggler } from 'reactstrap';
import LoadingBar from 'react-redux-loading-bar';

import { Brand } from './header-components';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCategoryEntities } from 'app/entities/category/category.reducer';
import { getEntities as getSubcategoryEntities } from 'app/entities/subcategory/subcategory.reducer';

const CATEGORY_FETCH_SIZE = 2000;
const SUBCATEGORY_FETCH_SIZE = 5000;

export interface IHeaderProps {
  isAuthenticated: boolean;
  isAdmin: boolean;
  ribbonEnv: string;
  isInProduction: boolean;
  isOpenAPIEnabled: boolean;
}

const Header = (props: IHeaderProps) => {
  const dispatch = useAppDispatch();
  const [menuOpen, setMenuOpen] = useState(false);
  const [isCategoriesOpen, setIsCategoriesOpen] = useState(false);
  const [activeCategoryId, setActiveCategoryId] = useState<number | null>(null);
  const closeCategoriesTimeoutRef = useRef<number | null>(null);

  const toggleMenu = () => setMenuOpen(!menuOpen);

  const categories = useAppSelector(state => state.category.entities).filter(category => category.active !== false);
  const categoriesLoading = useAppSelector(state => state.category.loading);
  const subcategories = useAppSelector(state => state.subcategory.entities).filter(subcategory => subcategory.active !== false);

  useEffect(() => {
    dispatch(getCategoryEntities({ page: 0, size: CATEGORY_FETCH_SIZE, sort: 'name,asc' }));
    dispatch(getSubcategoryEntities({ page: 0, size: SUBCATEGORY_FETCH_SIZE, sort: 'name,asc' }));
  }, [dispatch]);

  useEffect(() => {
    if (!activeCategoryId && categories.length > 0) {
      setActiveCategoryId(categories[0].id ?? null);
    }
  }, [categories, activeCategoryId]);

  const activeCategory = useMemo(
    () => categories.find(category => category.id === activeCategoryId) ?? categories[0],
    [categories, activeCategoryId],
  );

  const normalizedSubcategories = useMemo(
    () =>
      subcategories
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
    [subcategories],
  );

  const activeCategorySubcategories = useMemo(
    () =>
      normalizedSubcategories
        .filter(subcategory => subcategory.categoryId === activeCategory?.id)
        .map(subcategory => subcategory.name)
        .filter(Boolean) as string[],
    [normalizedSubcategories, activeCategory?.id],
  );

  const subcategoryColumns = useMemo(() => {
    const columnCount = activeCategorySubcategories.length > 20 ? 3 : 2;
    const chunkSize = Math.max(1, Math.ceil(activeCategorySubcategories.length / columnCount));
    return Array.from({ length: columnCount }, (_, index) => activeCategorySubcategories.slice(index * chunkSize, (index + 1) * chunkSize));
  }, [activeCategorySubcategories]);

  const openCategoriesMenu = () => {
    if (closeCategoriesTimeoutRef.current) {
      window.clearTimeout(closeCategoriesTimeoutRef.current);
      closeCategoriesTimeoutRef.current = null;
    }
    setIsCategoriesOpen(true);
  };

  const closeCategoriesMenuWithDelay = () => {
    if (closeCategoriesTimeoutRef.current) {
      window.clearTimeout(closeCategoriesTimeoutRef.current);
    }
    closeCategoriesTimeoutRef.current = window.setTimeout(() => {
      setIsCategoriesOpen(false);
      closeCategoriesTimeoutRef.current = null;
    }, 180);
  };

  useEffect(
    () => () => {
      if (closeCategoriesTimeoutRef.current) {
        window.clearTimeout(closeCategoriesTimeoutRef.current);
      }
    },
    [],
  );

  /* jhipster-needle-add-element-to-menu - JHipster will add new menu items here */

  return (
    <div id="app-header">
      <LoadingBar className="loading-bar" />
      <Navbar data-cy="navbar" expand="md" fixed="top" className="jh-navbar">
        <NavbarToggler aria-label="Menu" onClick={toggleMenu} />
        <Brand />
        <Collapse isOpen={menuOpen} navbar>
          <Nav id="header-tabs" className="header-main-nav" navbar>
            <div className="categories-menu-wrapper" onMouseEnter={openCategoriesMenu} onMouseLeave={closeCategoriesMenuWithDelay}>
              <button
                type="button"
                className="header-link categories-trigger"
                onClick={() => (isCategoriesOpen ? closeCategoriesMenuWithDelay() : openCategoriesMenu())}
                aria-expanded={isCategoriesOpen}
              >
                Categories <span className="caret">v</span>
              </button>

              {isCategoriesOpen ? (
                <div className="categories-mega-menu">
                  <aside className="category-list">
                    {categoriesLoading && <div className="category-state">Loading categories...</div>}
                    {!categoriesLoading &&
                      categories.map(category => (
                        <button
                          type="button"
                          key={category.id}
                          className={`category-list-item ${category.id === activeCategory?.id ? 'active' : ''}`}
                          onMouseEnter={() => setActiveCategoryId(category.id ?? null)}
                          onClick={() => setActiveCategoryId(category.id ?? null)}
                        >
                          <span className="bullet" />
                          {category.name}
                        </button>
                      ))}
                  </aside>
                  <section className="category-details">
                    <header>
                      <h4>{activeCategory?.name ?? 'Categories'}</h4>
                      <RouterLink to="/offer" className="view-all">
                        View all
                      </RouterLink>
                    </header>
                    <div className="subcategory-columns">
                      {subcategoryColumns.map((column, index) => (
                        <div key={`column-${index}`} className="subcategory-column">
                          {column.length > 0 ? (
                            column.map(subcategory => (
                              <RouterLink key={subcategory} to="/offer" className="subcategory-link">
                                {subcategory}
                              </RouterLink>
                            ))
                          ) : (
                            <span className="subcategory-empty">No subcategories</span>
                          )}
                        </div>
                      ))}
                    </div>
                  </section>
                </div>
              ) : null}
            </div>

            <RouterLink className="header-link" to="/offer">
              Explore
            </RouterLink>
            <RouterLink className="header-link" to="/">
              How it Works
            </RouterLink>
          </Nav>

          <Nav className="header-right-nav" navbar>
            <form className="header-search" onSubmit={event => event.preventDefault()}>
              <input type="text" placeholder="Search..." aria-label="Search" />
            </form>

            {props.isAuthenticated ? (
              <>
                <RouterLink className="header-auth-link" to="/account/settings">
                  Settings
                </RouterLink>
                <RouterLink className="header-signup-btn" to="/logout">
                  Sign Out
                </RouterLink>
              </>
            ) : (
              <>
                <RouterLink className="header-auth-link" to="/login">
                  Log in
                </RouterLink>
                <RouterLink className="header-signup-btn" to="/account/register">
                  Sign Up
                </RouterLink>
              </>
            )}
          </Nav>
        </Collapse>
      </Navbar>
    </div>
  );
};

export default Header;
