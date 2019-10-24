import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import cn from 'classnames';
import {
  isEmpty,
  filter,
  find,
  isEqual,
  has,
  get,
  clone,
  setWith,
  curry,
} from 'lodash';
import { createStructuredSelector } from 'reselect';
import { compose, withPropsOnChange, branch, getContext } from 'recompose';
import pathToRegexp from 'path-to-regexp';

import Section from '../layouts/Section';
import Factory from '../../core/factory/Factory';
import { LAYOUTS, REGIONS } from '../../core/factory/factoryLevels';
import BreadcrumbContainer from './Breadcrumb/BreadcrumbContainer';
import DocumentTitle from './DocumentTitle';
import Actions from '../actions/Actions';

import { metadataRequest, resetPage, mapUrl } from '../../actions/pages';
import {
  makePageMetadataByIdSelector,
  makePageLoadingByIdSelector,
  makePageErrorByIdSelector,
  makePageDisabledByIdSelector,
  makePageStatusByIdSelected,
} from '../../selectors/pages';
import { getLocation } from '../../selectors/global';
import withActions from './withActions';
import Alert from '../snippets/Alerts/Alert';

class PageContainer extends React.Component {
  componentDidMount() {
    this.props.getMetadata();
  }

  componentWillUnmount() {
    this.props.reset(this.props.pageId);
  }

  componentDidUpdate(prevProps) {
    const { metadata, getMetadata, routeMap, reset, error } = this.props;

    if (!isEmpty(metadata) && this.shouldGetPageMetadata(prevProps)) {
      reset(prevProps.pageId);
      getMetadata();
    } else if (
      this.isEqualPageId(prevProps) &&
      !this.isEqualPageUrl(prevProps)
    ) {
      routeMap();
      if (error) {
        getMetadata();
      }
    }
  }

  shouldGetPageMetadata(prevProps) {
    const {
      metadata,
      location: { pathname, state = {} },
    } = this.props;
    if (!isEmpty(metadata) && !isEmpty(metadata.routes)) {
      const findedRoutes = filter(metadata.routes.list, route => {
        const re = pathToRegexp(route.path);
        return re.test(pathname);
      });
      const isNewPage = find(findedRoutes, route => {
        return route.isOtherPage;
      });
      return (
        (isNewPage ||
          (this.isEqualPageId(prevProps) &&
            !this.isEqualPageUrl(prevProps) &&
            isEmpty(findedRoutes))) &&
        !state.silent
      );
    }
    return false;
  }

  getErrorPage() {
    const { status } = this.props;
    const { defaultErrorPages } = this.context;
    return get(
      find(defaultErrorPages, page => page.status === status),
      'component',
      null
    );
  }

  isEqualPageId(prevProps) {
    return this.props.pageId === prevProps.pageId;
  }

  isEqualPageUrl(prevProps) {
    return this.props.pageUrl === prevProps.pageUrl;
  }

  isEqualLocation(prevProps) {
    return isEqual(this.props.location, prevProps.location);
  }

  render() {
    const {
      metadata,
      defaultTemplate: Template = React.Fragment,
      toolbar,
      actions,
      containerKey,
      error,
      disabled,
      pageId,
    } = this.props;

    const errorPage = this.getErrorPage();

    return errorPage ? (
      React.createElement(errorPage)
    ) : (
      <div className={cn({ 'n2o-disabled-page': disabled })}>
        {error && <Alert {...error} visible />}
        {!isEmpty(metadata) && metadata.page && (
          <DocumentTitle {...metadata.page} />
        )}
        {!isEmpty(metadata) && metadata.breadcrumb && (
          <BreadcrumbContainer
            defaultBreadcrumb={this.context.defaultBreadcrumb}
            items={metadata.breadcrumb}
          />
        )}
        {toolbar && (toolbar.topLeft || toolbar.topRight) && (
          <div className="n2o-page-actions">
            <Actions
              toolbar={toolbar.topLeft}
              actions={actions}
              containerKey={containerKey}
              pageId={pageId}
            />
            <Actions
              toolbar={toolbar.topRight}
              actions={actions}
              containerKey={containerKey}
              pageId={pageId}
            />
          </div>
        )}
        <div className="n2o-page">
          {has(metadata, 'layout') && (
            <Factory
              level={LAYOUTS}
              src={metadata.layout.src}
              {...metadata.layout}
            >
              {Object.keys(metadata.layout.regions).map((place, i) => {
                return (
                  <Section place={place} key={'section' + i}>
                    {metadata.layout.regions[place].map((region, j) => (
                      <Factory
                        key={`region-${place}-${j}`}
                        level={REGIONS}
                        {...region}
                        pageId={metadata.id}
                      />
                    ))}
                  </Section>
                );
              })}
            </Factory>
          )}
        </div>
        {toolbar && (toolbar.bottomLeft || toolbar.bottomRight) && (
          <div className="n2o-page-actions">
            <Actions
              toolbar={toolbar.bottomLeft}
              actions={actions}
              containerKey={containerKey}
              pageId={pageId}
            />
            <Actions
              toolbar={toolbar.bottomRight}
              actions={actions}
              containerKey={containerKey}
              pageId={pageId}
            />
          </div>
        )}
      </div>
    );
  }
}

PageContainer.contextTypes = {
  defaultBreadcrumb: PropTypes.oneOfType([
    PropTypes.func,
    PropTypes.element,
    PropTypes.node,
  ]),
  defaultErrorPages: PropTypes.arrayOf(
    PropTypes.oneOfType([PropTypes.node, PropTypes.element, PropTypes.func])
  ),
};

PageContainer.propTypes = {
  metadata: PropTypes.object,
  loading: PropTypes.bool,
  pageId: PropTypes.string,
  pageUrl: PropTypes.string,
  pageMapping: PropTypes.object,
  rootPage: PropTypes.bool,
  status: PropTypes.number,
};

PageContainer.defaultProps = {
  rootPage: false,
};

const mapStateToProps = createStructuredSelector({
  metadata: (state, props) => {
    return makePageMetadataByIdSelector(props.pageId)(state, props);
  },
  loading: (state, props) => {
    return makePageLoadingByIdSelector(props.pageId)(state, props);
  },
  error: (state, { pageId }) => {
    return makePageErrorByIdSelector(pageId)(state);
  },
  disabled: (state, { pageId }) => {
    return makePageDisabledByIdSelector(pageId)(state);
  },
  status: (state, { pageId }) => makePageStatusByIdSelected(pageId)(state),
  location: getLocation,
});

function mapDispatchToProps(
  dispatch,
  { pageId, rootPage, pageUrl, pageMapping }
) {
  return {
    getMetadata: () => {
      dispatch(metadataRequest(pageId, rootPage, pageUrl, pageMapping));
    },
    reset: pageId => dispatch(resetPage(pageId)),
    routeMap: () => dispatch(mapUrl(pageId)),
  };
}

export { PageContainer };

export default compose(
  branch(({ rootPage }) => rootPage, withActions),
  withPropsOnChange(['pageId', 'pageUrl'], ({ pageId, pageUrl }) => ({
    pageId: pageId ? pageId : pageUrl ? pageUrl : null,
  })),
  connect(
    mapStateToProps,
    mapDispatchToProps
  )
)(PageContainer);