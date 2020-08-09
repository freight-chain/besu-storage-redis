/*
 * Copyright ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.hyperledger.besu.ethereum.worldstate;

import org.hyperledger.besu.ethereum.core.AbstractWorldUpdater.UpdateTrackingAccount;
import org.hyperledger.besu.ethereum.core.Account;
import org.hyperledger.besu.ethereum.core.Address;
import org.hyperledger.besu.ethereum.core.DefaultEvmAccount;
import org.hyperledger.besu.ethereum.core.Wei;
import org.hyperledger.besu.ethereum.core.WorldUpdater;

import java.util.Collection;
import java.util.Optional;

public class DefaultMutablePrivateWorldStateUpdater implements WorldUpdater {

  private final WorldUpdater publicWorldUpdater;
  private final WorldUpdater privateWorldUpdater;

  public DefaultMutablePrivateWorldStateUpdater(
      final WorldUpdater publicWorldUpdater, final WorldUpdater privateWorldUpdater) {
    this.publicWorldUpdater = publicWorldUpdater;
    this.privateWorldUpdater = privateWorldUpdater;
  }

  @Override
  public DefaultEvmAccount createAccount(
      final Address address, final long nonce, final Wei balance) {
    return privateWorldUpdater.createAccount(address);
  }

  @Override
  public DefaultEvmAccount createAccount(final Address address) {
    return privateWorldUpdater.createAccount(address);
  }

  @Override
  public DefaultEvmAccount getOrCreate(final Address address) {
    return privateWorldUpdater.getOrCreate(address);
  }

  @Override
  public DefaultEvmAccount getAccount(final Address address) {
    final DefaultEvmAccount privateAccount = privateWorldUpdater.getAccount(address);
    if (privateAccount != null && !privateAccount.isEmpty()) {
      return privateAccount;
    }
    final DefaultEvmAccount publicAccount = publicWorldUpdater.getAccount(address);
    if (publicAccount != null && !publicAccount.isEmpty()) {
      publicAccount.setImmutable(true);
      return publicAccount;
    }
    return privateAccount;
  }

  @Override
  public void deleteAccount(final Address address) {
    privateWorldUpdater.deleteAccount(address);
  }

  @Override
  public Collection<UpdateTrackingAccount<? extends Account>> getTouchedAccounts() {
    return privateWorldUpdater.getTouchedAccounts();
  }

  @Override
  public Collection<Address> getDeletedAccountAddresses() {
    return privateWorldUpdater.getDeletedAccountAddresses();
  }

  @Override
  public void revert() {
    privateWorldUpdater.revert();
  }

  @Override
  public void commit() {
    privateWorldUpdater.commit();
  }

  @Override
  public Account get(final Address address) {
    final Account privateAccount = privateWorldUpdater.get(address);
    if (privateAccount != null && !privateAccount.isEmpty()) {
      return privateAccount;
    }
    return publicWorldUpdater.get(address);
  }

  @Override
  public WorldUpdater updater() {
    return this;
  }

  @Override
  public Optional<WorldUpdater> parentUpdater() {
    return privateWorldUpdater.parentUpdater();
  }
}
