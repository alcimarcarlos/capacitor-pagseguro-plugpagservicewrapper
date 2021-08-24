import { WebPlugin } from '@capacitor/core';
import { PlugPagServiceWrapperPlugin } from './definitions';

export class PlugPagServiceWrapperWeb extends WebPlugin implements PlugPagServiceWrapperPlugin {
  constructor() {
    super({
      name: 'PlugPagServiceWrapper',
      platforms: ['web'],
    });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }

  async getLibVersion(): Promise<{ results: any[] }> {
    console.log('LibVersion');
    return {
      results: []
    };
  }

  async reprintStablishmentReceipt(): Promise<{ results: any[] }> {
    console.log('ReprintStablishmentReceipt');
    return {
      results: []
    };
  }

  async reprintCustomerReceipt(): Promise<{ results: any[] }> {
    console.log('ReprintCustomerReceipt');
    return {
      results: []
    };
  }

  async getLastApprovedTransaction(): Promise<{ results: any[] }> {
    console.log('GetLastApprovedTransaction',);
    return {
      results: []
    };
  }

  async createPayment(request: {
    reference: string,
    type:string, 
    installments:string,
    amount: string
  }): Promise<{ results: any[] }> {
    console.log('createPayment request', request);
    return {
      results: []
    };
  }

}

const PlugPagServiceWrapper = new PlugPagServiceWrapperWeb();

export { PlugPagServiceWrapper };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(PlugPagServiceWrapper);
