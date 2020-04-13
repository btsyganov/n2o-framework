import React from 'react';

import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import isUndefined from 'lodash/isUndefined';
import { getModelSelector } from '../../selectors/models';
import propsResolver from '../../utils/propsResolver';
import { createStructuredSelector } from 'reselect';

function PageTitle({ title, model }) {
  let resolveTitle = title;
  if (title && model) {
    resolveTitle = propsResolver(title, model);
  }

  return (
    !isUndefined(resolveTitle) && resolveTitle !== '' && <h1>{resolveTitle}</h1>
  );
}

PageTitle.propTypes = {
  title: PropTypes.string,
  modelLink: PropTypes.string,
};

const mapStateToProps = createStructuredSelector({
  model: (state, { modelLink }) => getModelSelector(modelLink)(state),
});

export default connect(mapStateToProps)(PageTitle);
