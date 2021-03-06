import React from 'react';
import ReactDOM from 'react-dom';
import N2O from 'n2o-framework';
import createFactoryConfig from "n2o-framework/lib/core/factory/createFactoryConfig";
import functions from "n2o-framework/lib/utils/functions";
import { authProvider } from 'n2o-auth';

import 'n2o-framework/dist/n2o.css';
import './index.css';

const config = {
  evalContext: functions,
  security: {
    authProvider,
    externalLoginUrl: '/'
  },
  messages: {
    timeout: {
      error: 0,
      success: 5000,
      warning: 0,
      info: 0,
    }
  }
};

ReactDOM.render(<N2O {...createFactoryConfig(config)} />, document.getElementById('n2o'));