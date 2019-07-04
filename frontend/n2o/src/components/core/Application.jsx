import React from 'react';
import PropTypes from 'prop-types';
import { bindActionCreators } from 'redux';
import { get, isFunction, isBoolean } from 'lodash';
import { connect } from 'react-redux';
import {
  compose,
  withContext,
  branch,
  renderComponent,
  lifecycle,
} from 'recompose';
import { createStructuredSelector } from 'reselect';
import numeral from 'numeral';
import 'numeral/locales/ru';
import { requestConfig as requestConfigAction } from '../../actions/global';
import { globalSelector } from '../../selectors/global';
import Spinner from '../snippets/Spinner/Spinner';
import Alert from '../snippets/Alerts/Alert';

numeral.locale('ru');

function Application({ locale, loading, messages, render }) {
  return (
    <Spinner type="cover" loading={loading}>
      {render(locale, messages)}
    </Spinner>
  );
}

Application.propTypes = {
  locale: PropTypes.string,
  messages: PropTypes.object,
  menu: PropTypes.object,
  loading: PropTypes.bool,
  render: PropTypes.func,
  realTimeConfig: PropTypes.oneOfType([PropTypes.bool, PropTypes.func]),
  requestConfig: PropTypes.func,
  error: PropTypes.object,
};

const mapStateToProps = state => ({
  ...globalSelector(state),
});

const mapDispatchToProps = dispatch => ({
  dispatch,
  requestConfig: bindActionCreators(requestConfigAction, dispatch),
});

export default compose(
  connect(
    mapStateToProps,
    mapDispatchToProps
  ),
  withContext(
    {
      getLocale: PropTypes.func,
      getMessages: PropTypes.func,
      getMenu: PropTypes.func,
      getFromConfig: PropTypes.func,
    },
    props => ({
      getLocale: () => props.locale,
      getMessages: () => props.messages,
      getMenu: () => props.menu,
      getFromConfig: key => get(props, key),
    })
  ),
  lifecycle({
    componentDidMount() {
      const { realTimeConfig, requestConfig } = this.props;
      if (realTimeConfig) {
        requestConfig();
      }
    },
  })
)(Application);
